package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
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
    private final float DEFAULT_FEE = 4.5f;

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/saveOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject)  {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Save Order request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original saveOrder request");

            if (!canQueryVin(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1101, "数据维护中", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            }

            if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())) {
                //get cache and reset orderId
                JSONObject cacheResponse = JSONObject.parseObject(SaveOrderCache.getInstance().getByKey(originalRequest.getVin()).getResponseContent());
                JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(cacheResponse.get("data")));
                String orderNo = data.get("orderId").toString();
                String replaceOrderNo = generateOrderNo(Integer.parseInt(orderNo));
                OrderMap orderMap = new OrderMap();
                orderMap.setReplaceOrderNo(replaceOrderNo);
                orderMap.setOrderNo(orderNo);
                OrderMapCache.getInstance().addOrderMap(orderMap);
                data.put("orderId", replaceOrderNo);
                cacheResponse.put("data", data);

                logger.info("got from saveCache for vin: {}", originalRequest.getVin());

                //debit
                float balanceBeforeDebit = UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getBalance();

                float debitFee = getDebitFee(originalRequest.getPartnerId(), originalRequest.getVin());

                if (balanceBeforeDebit - debitFee < 0.00000001) {
                    AntResponse response = Authentication.genAntResponse(1002, "账户余额不足", logger);
                    return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
                }
                debit(originalRequest, replaceOrderNo, balanceBeforeDebit, debitFee);

//                if (Authentication.authenticateCallBackUrl(clientRequest.getBody().getCallBackUrl())) {
//                    clientRequest.getBody().setOrderNo(replaceOrderNo);
//                    callBack(clientRequest);
//
//                }
                logger.info("Return OK. {}", cacheResponse.toJSONString());
                return Response.status(Response.Status.OK).entity(cacheResponse).build();
//
            } else {

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
                    float balanceBeforeDebit = UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getBalance();
                    float debitFee = getDebitFee(originalRequest.getPartnerId(), originalRequest.getVin());

                    if (balanceBeforeDebit - debitFee < 0.00000001) {
                        AntResponse badResponse = Authentication.genAntResponse(1002, "账户余额不足", logger);
                        return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(badResponse)).build();
                    }
                    debit(originalRequest, saveOrder.getOrderNo(), balanceBeforeDebit, debitFee);

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

    private float getDebitFee(String partnerId, String vin) throws DBAccessException {
        float debitFee = 0f;

        VinBrand vinBrand = VinBrandCache.getInstance().getByKey(vin);
        if (vinBrand != null) {
            String brandId = vinBrand.getBrandId();
            if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId+"/"+brandId)) {
                debitFee = UserRateCache.getInstance().getByKey(partnerId+"/"+brandId).getPrice();
            } else if (UserRateCache.getInstance().getUserRateMap().containsKey(partnerId+"/"+"0")) {
                debitFee = UserRateCache.getInstance().getByKey(partnerId+"/"+"0").getPrice();
            } else if (BrandCache.getInstance().getById(brandId) != null) {
                debitFee = BrandCache.getInstance().getById(brandId).getPrice();
            }
        }

        //当找不到计费规则或出现0计费时
        if (debitFee - 0 < 0.00000001) {
            debitFee = DEFAULT_FEE;
        }
        return debitFee;
    }

    private void debit(AntRequest request, String orderId, float balanceBeforeDebit, float debitFee) throws DBAccessException {
        DebitLog debitLog = new DebitLog();
        debitLog.setPartnerId(request.getPartnerId());
        debitLog.setBalanceBeforeDebit(balanceBeforeDebit);
        debitLog.setDebitFee(debitFee);
        debitLog.setOrderNo(orderId);
        debitLog.setVin(request.getVin());
        VinBrand vinBrand = VinBrandCache.getInstance().getByKey(request.getVin());
        if (vinBrand!=null) {
            debitLog.setBrandId(vinBrand.getBrandId());
            debitLog.setBrandName(vinBrand.getBrandName());
        }
        DebitLogAccessor.getInstance().addLog(debitLog);

        User updatedUser = UserCache.getInstance().getByKey(request.getPartnerId());
        updatedUser.setBalance(balanceBeforeDebit - debitFee);
        UserCache.getInstance().updateUser(updatedUser);
    }

    private boolean canQueryVin(Object requestObject) throws Exception {
        AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
        if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())) {
            return true;
        } else {
            originalRequest.setSign(null);
            originalRequest.setPartnerId(PropertyUtil.readValue("app.key"));
            originalRequest.setSign(EncryptUtil.getAntSign(originalRequest, PropertyUtil.readValue("app.secret")));

            logger.info("Request to source with: {}", originalRequest.toString());
            TransLogAccessor.getInstance().AddTransLog(JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class), originalRequest.toString(), "source checkVin request");

            String url = PropertyUtil.readValue("source.url") + "/api/checkVin";
            webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, originalRequest);

            AntResponse sourceResponse = response.getEntity(AntResponse.class);
            logger.info("Got response: {}", sourceResponse);
            TransLogAccessor.getInstance().AddTransLog(JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class), sourceResponse.toString(), "source checkVin response");

            if (sourceResponse.getCode() == 1106) {
                if (VinBrandCache.getInstance().getByKey(originalRequest.getVin()) == null) {
                    VinBrand vinBrand = new VinBrand();
                    vinBrand.setVin(originalRequest.getVin());
                    vinBrand.setBrandId(String.valueOf(sourceResponse.getData().getBrandId()));
                    vinBrand.setBrandName(sourceResponse.getData().getBrandName());
                    VinBrandCache.getInstance().addVinBrand(vinBrand);
                }
                return true;
            }
        }
        return false;
    }

    private String generateOrderNo(int oldNo) {
        int ts = (int)(System.currentTimeMillis()/1000);
        int seed = ts - 1100000000;
        return String.valueOf(seed+oldNo);
    }

}
