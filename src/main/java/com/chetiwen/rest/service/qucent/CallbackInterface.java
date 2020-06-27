package com.chetiwen.rest.service.qucent;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.GetOrderCache;
import com.chetiwen.cache.OrderCallbackCache;
import com.chetiwen.cache.OrderReportCache;
import com.chetiwen.common.LogType;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.TransactionLog;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.OrderReportResponse;
import com.chetiwen.object.qucent.QucentOrderResponse;
import com.chetiwen.server.qucent.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/api")
public class CallbackInterface {
    private static Logger logger = LoggerFactory.getLogger(com.chetiwen.rest.service.antqueen.CallbackInterface.class);
    private static RSAUtil rsaUtil = new RSAUtil();

    // 公钥
    private static String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
            "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
            "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
            "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
            "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
            "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
            "4wIDAQAB";

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
            content = DataConvertor.convertUnicode(rsaUtil.decryptByPublicKey(puk, content));
        }

        TransactionLog log = new TransactionLog();
        log.setLogType(LogType.QUCENT_CALLBACK);
        log.setUserName("CALLBACK");
        log.setPartnerId("SYSTEM");
        log.setTransactionContent(content);
        TransLogAccessor.getInstance().addLog(log);


        QucentOrderResponse qucentOrderResponse = JSONObject.parseObject(content, QucentOrderResponse.class);
        if (qucentOrderResponse.getCode() == 0) {
            logger.info("qucent GID is {}", qucentOrderResponse.getGid());
            if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(qucentOrderResponse.getGid())) {
                Order getOrder = new Order();
                getOrder.setOrderNo(qucentOrderResponse.getGid());
                AntOrderResponse orderResponse = DataConvertor.convertToAntQueenOrder(qucentOrderResponse);
                getOrder.setResponseContent(orderResponse.toString());
                GetOrderCache.getInstance().addGetOrder(getOrder);
            }

            if (!OrderReportCache.getInstance().getOrderReportMap().containsKey(qucentOrderResponse.getGid())) {
                Order getOrder = new Order();
                getOrder.setOrderNo(qucentOrderResponse.getGid());
                OrderReportResponse orderReportResponse = DataConvertor.convertToAntQueenReport(qucentOrderResponse);
                getOrder.setResponseContent(orderReportResponse.toString());
                GetOrderCache.getInstance().addGetOrder(getOrder);
            }

            if (OrderCallbackCache.getInstance().getByKey(qucentOrderResponse.getGid()) != null) {
                new CallbackProcessor().callback(OrderCallbackCache.getInstance().getByKey(qucentOrderResponse.getGid()).getUrl(),
                        OrderCallbackCache.getInstance().getByKey(qucentOrderResponse.getGid()).getOrderNo());
            }
        } else {
            //TODO: 对已收款退费，同时不再支持该订单的查询
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", 0);

        return Response.status(Response.Status.OK).entity(jsonResponse.toJSONString()).build();
    }
}
