package com.chetiwen.rest.service.qucent;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.controll.DebitComputer;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
import com.chetiwen.server.qucent.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.*;


@Path("/api/b2b")
public class SaveOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(SaveOrderInterface.class);

    private static RSAUtil rsaUtil = new RSAUtil();
    private static String CUSTOMER_ID = "0f092952d9a2f7a0c0faea927e178396";
    // 地址
    private static String URL = "https://entapi.qucent.cn/api/v3";
    // 公钥
    private static String puk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1f3Hq0aM6HVRZ3Si7XA" +
            "KSNeNsAocmLYC/4YA1MShi42jZEP/gTOQkzvOnUu0uM/YFQZmMgy5LjW/SUrCbQE" +
            "An7LCKJ3DYUAblO+c5WalZxVEcOZ0M8IwJRD5WQe9bcDcp3xq2+5rD/a5g3XmenY" +
            "UIG693zyEFTphyEOsikkIXmxPrmqjOm12369HkUPnUQII3uH4fCHwFVm7bTtUhmq" +
            "6K/TOOFW8CB4Bk8QWeJ9WsnxSQo/0MPTwf45YKKtqqVbrc+QO+4lxuMC6E40qMfh" +
            "357qWUG1h3om/TP6O94vw9NTPTzQUzP66Hk3Mt4iTjAi+7jx7Y8NNupwHs0QzQG7" +
            "4wIDAQAB";
    // 私钥
    private static String pik = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDXOzLxWRGN7Hsa" +
            "GGueGj4BwYYkoJG9bWwpaKBtyeKJJxbEmlOjEMJ9ltvBuXb0SvYZb9sCaEHwSRO6" +
            "5Af7dbbuRXjIuhPd/Ko/DSxNhP8CQDZp+btGNWCJ8MccL/AA0rvevXDZlKmop9i+" +
            "PDf+qyBy4saw5pWZzwtAetwVoN+tnatfT5JRC/bjgYOwUWZpzaBka2+gjy/b6TDb" +
            "81CxKu+r1D2yM3KF6fWcythqWKOWLqYQirsjscicraD2a8tdRaEWQhD1thjxf4e+" +
            "B7wfzjsqHANI02tt3tLOnfGYbrrOCwP4rbEIm5bQ74XZDSKWXSmfZkB8xlkxn2Hd" +
            "LT6eZqpDAgMBAAECggEBAJDYlUMZZxf1Qy9fqeU/0eUKoUU7DnnGDxmbAQSB7kPR" +
            "G6B6H7lJCSrOANzR/P0RCag6v9BR67ZS58VJuxl+sfqOpGep2r71UHmYWu1ciOWx" +
            "4yzU1TS9rVeHw+fzVvim2apgIXc8diU7uEDmc+Ses/q9JWxd8eYOEYt2Y3Dm0EGc" +
            "82niEve7B9D3vfmj0cjOEYKIxvbNPDq8Xi6ZQjNh5JGxlfPyUTF1UxUXZyBn9eoo" +
            "8IaC+tBPxYl9phtIYmNhfst4Yqm7QEb7tAMKQlzJudjrnZdFWYandHgGWMSUiVCW" +
            "le5pzFC+Fk6is9xqYNJuwk1XJUyNKfvziWZrPyqWGxECgYEA8ZF1WGlcBPQxN4bs" +
            "Rt0Ib3DFevPIT8OiuHD0yX6CzZUxIDWfviCdek5AXS7ZMbpb3rpDvdtLtL1ZqeoG" +
            "6yujlwVWllZ4QD82xHvurHAFERwRqunxQ6cx7R4rORbYXHv6XfZyhEvI8y/1Vck5" +
            "fH/n6/eJAGxuuo/I2aAK1vdToqkCgYEA5BbxjIHxdJKpZPGoP3Ph1XFnObhJIs9a" +
            "HNrV2MMtog8UO2YouWf4t1siLp3JsSAMXYEI7XbdZG9Zih5yzt0PCa7mrBd3mXlr" +
            "WE8ghfePx2sI4qXacsqNgzLWcjXbRDZDS0aCUew8bbnBhGXnUXqmIduzPBtnEs+d" +
            "HpgtaBDSZQsCgYAQ86svbB1X/6bghahZBLPN1jUVfrwE1O67ULns1eLp+Fk9MGYo" +
            "WnOSnKEpqNr3AWPnCl0smpICefMr2E9p+2L8exRrcl/36je2rBfApA/G9phKzSXw" +
            "IHCBekeANxkxzEVyiJPastLENg5aWced8//bcEB99h4DG4n1s6RvF2YYGQKBgQCZ" +
            "RCmP5wle8eZN5GzQJohMKuXYTVMnxvmghhRIke6qBUPtHhqja5AfdWekt3Z+RTDJ" +
            "7BkZqFPgV0ptm0Q+aSDfut1aKnK9eG9/abxLCS1eLThNRHFjzWQGEzUyjaoHTgcu" +
            "H/UWI43/lWDKHMexYp8cBUuNSkSayVOk6VEpqpQWPQKBgQCgPSgjV6SvxKnkSgoP" +
            "dFfEt5ovIn9qyFLrgSsH0gVUbqRwpIAjL8aGrF9tCN4MHBFAxi6eKqBXo1NZi1nW" +
            "rm05RCOknVnkP7rMvUCwPKMynPwfttM2G7r3gQP6MRXYMaPlflFtLK9E419gQYs5" +
            "RDrHXfQBPozbYKC0IAWTLkPPIw==";


    @POST
    @Path("/saveOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject)  {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Place Order request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), ConstData.CLIENT_QUERYVIN_REQUEST);

            float debitFee = DebitComputer.getDebitFee(originalRequest.getPartnerId(), originalRequest.getVin());
            if (UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getBalance() - debitFee < 0) {
                AntResponse response = Authentication.genAntResponse(1002, "账户余额不足", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())
                && ConstData.DATA_SOURCE_QUCENT.equals(SaveOrderCache.getInstance().getSaveOrderMap().get(originalRequest.getVin()).getDataSource()) ) {
                //get cache and reset orderId
                JSONObject cacheResponse = JSONObject.parseObject(SaveOrderCache.getInstance().getByKey(originalRequest.getVin()).getResponseContent());
                JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(cacheResponse.get("data")));
                String orderNo = data.get("orderId").toString();
                String replaceOrderNo = Authentication.generateOrderNo();
                OrderMap orderMap = new OrderMap();
                orderMap.setReplaceOrderNo(replaceOrderNo);
                orderMap.setOrderNo(orderNo);
                OrderMapCache.getInstance().addOrderMap(orderMap);
                data.put("orderId", replaceOrderNo);
                cacheResponse.put("data", data);

                logger.info("got from saveCache for vin: {}", originalRequest.getVin());

                if (GetOrderCache.getInstance().getGetOrderMap().containsKey(orderNo)) {
                    DebitComputer.debit(originalRequest, replaceOrderNo, debitFee, ConstData.FEE_TYPE_TRUE);
                } else {
                    DebitComputer.debit(originalRequest, replaceOrderNo, debitFee, ConstData.FEE_TYPE_FALSE);
                }

                if (originalRequest.getCallbackUrl() != null) {
                    new CallbackProcessor().callback(originalRequest.getCallbackUrl(), orderNo);
                }

                logger.info("Return OK. {}", cacheResponse.toJSONString());
                return Response.status(Response.Status.OK).entity(cacheResponse).build();
            } else {
                JSONObject qucentResponse = askSource(originalRequest);

                if ("0".equals(String.valueOf(qucentResponse.get("code"))) || "300018".equals(String.valueOf(qucentResponse.get("code")))) {
                    //cache it
                    logger.info("Cached antResponse: {}", qucentResponse.toJSONString());

                    JSONObject antResponse = new JSONObject();
                    JSONObject data = new JSONObject();
                    antResponse.put("code", 0);
                    antResponse.put("msg", "success");
                    antResponse.put("data", data);
                    data.put("orderId", String.valueOf(qucentResponse.get("gid")));

                    Order saveOrder = new Order();
                    saveOrder.setVin(originalRequest.getVin());
                    saveOrder.setOrderNo(data.get("orderId").toString());
                    saveOrder.setResponseContent(antResponse.toJSONString());
                    saveOrder.setDataSource(ConstData.DATA_SOURCE_QUCENT);
                    if (!SaveOrderCache.getInstance().getSaveOrderMap().containsKey(saveOrder.getVin())) {
                        SaveOrderCache.getInstance().addSaveOrder(saveOrder);
                    }

                    OrderMap orderMap = new OrderMap();
                    orderMap.setReplaceOrderNo(qucentResponse.get("userOrderId").toString());
                    orderMap.setOrderNo(saveOrder.getOrderNo());
                    OrderMapCache.getInstance().addOrderMap(orderMap);
                    data.put("orderId", orderMap.getReplaceOrderNo());

                    String feeType;
                    if ("true".equals(String.valueOf(qucentResponse.get("charge")))) {
                        feeType = ConstData.FEE_TYPE_TRUE;
                    } else {
                        feeType = ConstData.FEE_TYPE_FALSE;
                    }
                    //debit
                    DebitComputer.debit(originalRequest, orderMap.getReplaceOrderNo(), debitFee, feeType);

                    if (originalRequest.getCallbackUrl() != null) {
                        new CallbackProcessor().callback(originalRequest.getCallbackUrl(), saveOrder.getOrderNo());
                    }

                    logger.info("finish processing and return ok. {}", antResponse.toJSONString());
                    return Response.status(Response.Status.OK).entity(antResponse.toJSONString()).build();
                } else {
                    throw new RuntimeException(String.valueOf(qucentResponse.get("msg")));
                }
            }

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "品牌临时维护,下单未成功", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private JSONObject askSource(AntRequest originalRequest) throws Exception {

        JSONObject encrypt = new JSONObject();
        // 产品传入参数
        encrypt.put("vin", originalRequest.getVin());
        if (originalRequest.getEngineNum() != null) {
            encrypt.put("engine", originalRequest.getEngineNum());
        }

        String deCryptStr = JSONObject.toJSONString(encrypt);
        // 数据转为json字符串并加密
        String encrptedStr = rsaUtil.encryptByPrivateKey(pik, deCryptStr);

        // 生成订单编号
        String orderId = Authentication.generateOrderNo();
        // 生成时间戳
        String reqTime = System.currentTimeMillis() + "";

        // 生成签名
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("customerId", CUSTOMER_ID);
        paramMap.put("encrypt", deCryptStr);
        paramMap.put("userOrderId", orderId);
        paramMap.put("reqTime", reqTime);
        paramMap.put("encryptType", "false");
        paramMap.put("productCode", "BA610030");// 产品编号
        paramMap.put("version", "V001");// 版本号

        String signStr = ParamUtil.sortParam(paramMap);

        // 添加数据
        NameValuePair json1 = new BasicNameValuePair("customerId", CUSTOMER_ID);// 客户ID
        NameValuePair json2 = new BasicNameValuePair("encrypt", deCryptStr);// 加密后数据
        NameValuePair json3 = new BasicNameValuePair("userOrderId", orderId);// 订单号
        NameValuePair json4 = new BasicNameValuePair("reqTime", reqTime);// 时间戳
        NameValuePair json5 = new BasicNameValuePair("sign", MD5Util.encrypt(signStr));// 签名
        NameValuePair json6 = new BasicNameValuePair("encryptType", "false");// 是否加密
        NameValuePair json7 = new BasicNameValuePair("productCode", "BA610030");// 产品编号
        NameValuePair json8 = new BasicNameValuePair("version", "V001");// 版本号

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(json1);
        list.add(json2);
        list.add(json3);
        list.add(json4);
        list.add(json5);
        list.add(json6);
        list.add(json7);
        list.add(json8);

        TransactionLog log = new TransactionLog();
        log.setLogType(ConstData.QUCENT_QUERYVIN_REQUEST);
        log.setUserName(UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getUserName());
        log.setPartnerId(UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getPartnerId());
        log.setTransactionContent(list.toString());
        TransLogAccessor.getInstance().addLog(log);

        String response = HttpUtil.doPost(URL, list);
        logger.info("response from Qucent, {}",response);

        JSONObject result = JSONObject.parseObject(response);
        String encryptResult = String.valueOf(result.get("encrypt"));
        if (String.valueOf(result.get("encryptType")).equals("true")) {
            encryptResult = rsaUtil.decryptByPublicKey(puk, String.valueOf(result.get("encrypt")));
        }

        TransactionLog responseLog = new TransactionLog();
        responseLog.setLogType(ConstData.QUCENT_QUERYVIN_RESPONSE);
        responseLog.setUserName(UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getUserName());
        responseLog.setPartnerId(UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getPartnerId());
        responseLog.setTransactionContent(encryptResult);
        TransLogAccessor.getInstance().addLog(responseLog);

        return JSONObject.parseObject(encryptResult);
    }


}
