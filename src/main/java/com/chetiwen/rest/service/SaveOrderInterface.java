package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.OrderMapCache;
import com.chetiwen.cache.SaveOrderCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.OrderMap;
import com.chetiwen.object.AntRequest;
import com.chetiwen.object.AntResponse;
import com.chetiwen.util.EncryptUtil;
import com.chetiwen.util.PropertyUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api")
public class SaveOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(SaveOrderInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    @POST
    @Path("/saveOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject)  {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Save Order request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (restClient == null) {
                ClientConfig config = new DefaultClientConfig();
                config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                restClient = Client.create(config);
            }

            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original saveOrder request");



            if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())) {
                JSONObject response = JSONObject.parseObject(SaveOrderCache.getInstance().getByKey(originalRequest.getVin()).getResponseContent());

                JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(response.get("data")));
                String orderNo = data.get("orderId").toString();
                String replaceOrderNo = generateOrderNo(Integer.parseInt(orderNo));
                OrderMap orderMap = new OrderMap();
                orderMap.setReplaceOrderNo(replaceOrderNo);
                orderMap.setOrderNo(orderNo);
                OrderMapCache.getInstance().addOrderMap(orderMap);

                data.put("orderId", replaceOrderNo);
                response.put("data", data);

                logger.info("got from saveCache for vin: {}", originalRequest.getVin());

//                //debit
//                float balanceBeforeDebit = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getBalance();
//
//                float debitFee = getDebitFee(clientRequest, DEFAULT_FEE, response);
//
//                if (balanceBeforeDebit - debitFee < 0.00000001) {
//                    CarResponse badResponse = new CarResponse();
//                    badResponse.setCode("400015");
//                    badResponse.setMsg("余额不足，请充值!!");
//                    logger.info("return 400015, 余额不足，请充值!!");
//                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
//                }
//                debit(clientRequest, response, balanceBeforeDebit, debitFee);
//
//                if (Authentication.authenticateCallBackUrl(clientRequest.getBody().getCallBackUrl())) {
//                    clientRequest.getBody().setOrderNo(replaceOrderNo);
//                    callBack(clientRequest);
//
//                }
                logger.info("Return OK. {}", response.toJSONString());
                return Response.status(Response.Status.OK).entity(response).build();
//
            } else {
//                //debit
//                float balanceBeforeDebit = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getBalance();
//
//                if (balanceBeforeDebit - DEFAULT_FEE < 0.00000001) {
//                    CarResponse badResponse = new CarResponse();
//                    badResponse.setCode("400015");
//                    badResponse.setMsg("余额不足，请充值!!");
//                    logger.info("return 400015, 余额不足，请充值!!");
//                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
//                }
//
                JSONObject jsonRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject));

                jsonRequest.put("partnerId", PropertyUtil.readValue("app.key"));

//                if (ConnectionPool.isProduction) {
//                    jsonRequest.put("callBackUrl", PropertyUtil.readValue("call.back.url.production"));
//                } else {
//                    jsonRequest.put("callBackUrl", PropertyUtil.readValue("call.back.url"));
//                }

                jsonRequest.remove("sign");
                jsonRequest.put("sign", EncryptUtil.getAntSign(jsonRequest.toJSONString(), PropertyUtil.readValue("app.secret")));


                logger.info("Request to source with: {}", jsonRequest.toString());
                TransLogAccessor.getInstance().AddTransLog(originalRequest, jsonRequest.toString(), "source saveOrder request");

                String url = PropertyUtil.readValue("source.url") + "/api/queryByVin";
                webResource = restClient.resource(url);
                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,jsonRequest);
                JSONObject antResponse = response.getEntity(JSONObject.class);

                logger.info("get ant response: {}", antResponse.toJSONString());

                TransLogAccessor.getInstance().AddTransLog(originalRequest, antResponse.toJSONString(), "source saveOrder response");

                if ("0".equals(antResponse.get("code").toString())) {
                    //cache it
                    logger.info("Cached antResponse: {}", antResponse.toJSONString());

                    Order saveOrder = new Order();
                    saveOrder.setVin(originalRequest.getVin());
                    JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(antResponse.get("data")));
                    saveOrder.setOrderNo(data.get("orderId").toString());
                    saveOrder.setResponseContent(antResponse.toJSONString());
                    SaveOrderCache.getInstance().addSaveOrder(saveOrder);

                    //debit
//                    float debitFee = getDebitFee(clientRequest, DEFAULT_FEE, carResponse);
//
//                    if (balanceBeforeDebit - debitFee < 0.00000001) {
//                        CarResponse badResponse = new CarResponse();
//                        badResponse.setCode("400015");
//                        badResponse.setMsg("余额不足，请充值!!");
//                        logger.info("return 400015, 余额不足，请充值!!");
//                        return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
//                    }
//                    debit(clientRequest, carResponse, balanceBeforeDebit, debitFee);
//
//                    if (Authentication.authenticateCallBackUrl(clientRequest.getBody().getCallBackUrl())) {
//                        Thread.sleep(8000);
//                        callBack(clientRequest);
//                    }
                }

                logger.info("finish processing and return ok. {}", antResponse.toJSONString());
                return Response.status(Response.Status.OK).entity(antResponse.toJSONString()).build();
            }

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private String generateOrderNo(int oldNo) {
        int ts = (int)(System.currentTimeMillis()/1000);
        int seed = ts - 1100000000;
        return String.valueOf(seed+oldNo);
    }


}
