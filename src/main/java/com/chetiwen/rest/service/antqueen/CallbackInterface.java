package com.chetiwen.rest.service.antqueen;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.LogType;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.TransactionLog;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.AntResponse;
import com.chetiwen.server.qucent.RSAUtil;
import com.chetiwen.util.EncryptUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/api")
public class CallbackInterface {
    private static Logger logger = LoggerFactory.getLogger(CallbackInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    // 公钥
    public static String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
            "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
            "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
            "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
            "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
            "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
            "4wIDAQAB";

    @POST
    @Path("/callback/antqueen")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response AntQueenCallback(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Callback request from AntQueen : {}", requestObject);
        TransactionLog log = new TransactionLog();
        log.setLogType(LogType.ANTQUEEN_CALLBACK);
        log.setUserName("callback");
        log.setPartnerId("system");
        log.setTransactionContent(requestObject.toString());
        TransLogAccessor.getInstance().addLog(log);

        try {
            AntOrderResponse orderResponse = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntOrderResponse.class);

            if (orderResponse.getCode() == 0) {
                logger.info("orderResponse.getData().getOrderId() is {}", orderResponse.getData().getOrderId());
                if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(String.valueOf(orderResponse.getData().getOrderId()))) {
                    Order getOrder = new Order();
                    getOrder.setOrderNo(String.valueOf(orderResponse.getData().getOrderId()));
                    getOrder.setResponseContent(JSONObject.toJSONString(requestObject));
                    GetOrderCache.getInstance().addGetOrder(getOrder);
                }

                // TODO: look for callbackUrl cache
                if (OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())) != null) {
                    new CallbackProcessor().callback(OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())).getUrl(),
                            OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())).getOrderNo());
                }
            }

            AntResponse response = Authentication.genAntResponse(200, "success", logger);
            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();


        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(99999, "接收回调处理异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    @POST
    @Path("/callback/qucent")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response QucentCallback(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Callback request from Qucent {}", requestObject.toString());
        String content = null;
        String encryptType = null;
        try{
            JSONObject result = JSONObject.parseObject(requestObject.toString());
            content = result.get("encrypt").toString();
            encryptType = result.get("encryptType").toString();
        }catch (JSONException jsone) {
            String leftReplace = requestObject.toString().replace("{","");
            String rightReplace = leftReplace.replace("}", "");
            String[] abc = rightReplace.split(",");
            for (int i=0;i<abc.length;i++) {
                if (abc[i].contains("encrypt=")) {
                    content = abc[i].split("=")[1];
                }
                if (abc[i].contains("encryptType=")) {
                    encryptType = abc[i].split("=")[1];
                }
            }
        }

        if ("true".equals(encryptType) && content != null) {
            content = EncryptUtil.convertUnicode(new RSAUtil().decryptByPublicKey(puk, content));
        }

        TransactionLog log = new TransactionLog();
        log.setLogType(LogType.QUCENT_CALLBACK);
        log.setUserName("callback");
        log.setPartnerId("system");
        log.setTransactionContent(content);
        TransLogAccessor.getInstance().addLog(log);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", 0);

        return Response.status(Response.Status.OK).entity(jsonResponse.toJSONString()).build();
    }
}
