package com.chetiwen.rest.service.qucent;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.controll.DebitComputer;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.OrderReportResponse;
import com.chetiwen.object.qucent.QucentOrderResponse;
import com.chetiwen.server.qucent.RSAUtil;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/api")
public class CallbackInterface {
    private static Logger logger = LoggerFactory.getLogger(com.chetiwen.rest.service.antqueen.CallbackInterface.class);
    private static RSAUtil rsaUtil = new RSAUtil();

    private static Client restClient;
    private static WebResource webResource;

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

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
        logger.info("Received Callback request from Qucent: {}", requestObject.toString());

//        String url = "http://www.chetiwen.com:8139/api/callback/qucent";
//        webResource = restClient.resource(url);
//        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,requestObject);

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
        log.setLogType(ConstData.QUCENT_CALLBACK);
        log.setUserName("CALLBACK");
        log.setPartnerId("SYSTEM");
        log.setTransactionContent(content);
        TransLogAccessor.getInstance().addLog(log);


        QucentOrderResponse qucentOrderResponse = JSONObject.parseObject(content, QucentOrderResponse.class);
        logger.info("Qucent GID is {}", qucentOrderResponse.getGid());
        List<OrderMap> replacedNoList =  OrderMapCache.getInstance().getOrderMap().values()
                .stream().filter(mapping->mapping.getOrderNo().equals(qucentOrderResponse.getGid()))
                .collect(Collectors.toList());

        if (qucentOrderResponse.getCode() == 0) {
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
                getOrder.setVin(orderReportResponse.getData()==null?null:orderReportResponse.getData().getVin());
                OrderReportCache.getInstance().addOrderReport(getOrder);
            }

            //debit
            if ("true".equals(qucentOrderResponse.getCharge())) {
                String brandName = qucentOrderResponse.getData().getBasic().getBrand()==null?"普通品牌":qucentOrderResponse.getData().getBasic().getBrand();
                for (OrderMap orderMap : replacedNoList) {
                    DebitComputer.updateDebit(orderMap, brandName);

                    if (OrderCallbackCache.getInstance().getByKey(orderMap.getReplaceOrderNo()) != null) {
                        new CallbackProcessor().callback(OrderCallbackCache.getInstance().getByKey(orderMap.getReplaceOrderNo()).getUrl(),
                                OrderCallbackCache.getInstance().getByKey(orderMap.getReplaceOrderNo()).getOrderNo());
                    }
                }
            }

        } else {
            //删除计费记录，同时不再支持该订单的查询
            DebitComputer.deleteDebit(replacedNoList);
            SaveOrderCache.getInstance().delSaveOrder(qucentOrderResponse.getGid());
        }

        logger.info("###################################################################################################");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", 0);

        return Response.status(Response.Status.OK).entity(jsonResponse).build();
    }
}
