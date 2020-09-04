package com.chetiwen.rest.service.antqueen;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.controll.DebitComputer;
import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
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
import java.net.URLEncoder;


@Path("/api")
public class SaveOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(SaveOrderInterface.class);
    private static Client restClient;
    private static WebResource webResource;

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
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), ConstData.CLIENT_QUERYVIN_REQUEST);

            if (!canQueryVin(requestObject)) {  // 防止B端客户直接下单，没有checkVin而导致拿不到VinBrand信息而无法计费
                AntResponse response = Authentication.genAntResponse(1101, "数据维护中", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            float debitFee = DebitComputer.getDebitFee(originalRequest.getPartnerId(), originalRequest.getVin());
            if (UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getBalance() - debitFee < 0) {
                AntResponse response = Authentication.genAntResponse(1002, "账户余额不足", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())) {
                //get cache and reset orderId
                JSONObject cacheResponse = JSONObject.parseObject(SaveOrderCache.getInstance().getByVin(originalRequest.getVin()).getResponseContent());
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
                    new CallbackProcessor().callback(originalRequest.getCallbackUrl(), replaceOrderNo);
                }

                logger.info("Return OK. {}", cacheResponse.toJSONString());
                return Response.status(Response.Status.OK).entity(cacheResponse).build();
            } else {
                JSONObject antResponse = askSource(requestObject, originalRequest);

                logger.info("get ant response: {}", antResponse.toJSONString());
                TransLogAccessor.getInstance().AddTransLog(originalRequest, antResponse.toJSONString(), ConstData.ANTQUEEN_QUERYVIN_RESPONSE);

                if ("0".equals(antResponse.get("code").toString())) {
                    //cache it
                    logger.info("Cached antResponse: {}", antResponse.toJSONString());

                    Order saveOrder = new Order();
                    saveOrder.setVin(originalRequest.getVin());
                    JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(antResponse.get("data")));
                    saveOrder.setOrderNo(data.get("orderId").toString());
                    saveOrder.setResponseContent(antResponse.toJSONString());
                    saveOrder.setDataSource(ConstData.DATA_SOURCE_ANTQUEEN);
                    if (!SaveOrderCache.getInstance().getSaveOrderMap().containsKey(saveOrder.getVin())) {
                        SaveOrderCache.getInstance().addSaveOrder(saveOrder);
                    }

                    OrderMap orderMap = new OrderMap();
                    orderMap.setReplaceOrderNo(Authentication.generateOrderNo());
                    orderMap.setOrderNo(saveOrder.getOrderNo());
                    OrderMapCache.getInstance().addOrderMap(orderMap);
                    data.put("orderId", orderMap.getReplaceOrderNo());
                    antResponse.put("data", data);

                    //debit
                    DebitComputer.debit(originalRequest, orderMap.getReplaceOrderNo(), debitFee, ConstData.FEE_TYPE_FALSE);

                    if (originalRequest.getCallbackUrl() != null) {
                        new CallbackProcessor().callback(originalRequest.getCallbackUrl(), orderMap.getReplaceOrderNo());
                    }
                }

                logger.info("finish processing and return ok. {}", antResponse.toJSONString());
                return Response.status(Response.Status.OK).entity(antResponse.toJSONString()).build();
            }

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "品牌临时维护,下单未成功", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private JSONObject askSource(Object requestObject, AntRequest originalRequest) throws Exception {
        JSONObject jsonRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject));

        jsonRequest.put("partnerId", PropertyUtil.readValue("app.key"));
        String notEncodedUrl;
        if (ConnectionPool.isProduction) {
            notEncodedUrl = PropertyUtil.readValue("call.back.url.production");
        } else {
            notEncodedUrl = PropertyUtil.readValue("call.back.url");
        }
        jsonRequest.put("callbackUrl", URLEncoder.encode(notEncodedUrl,"utf-8"));
        jsonRequest.remove("sign");
        jsonRequest.put("sign", EncryptUtil.sign(jsonRequest.toJSONString(), PropertyUtil.readValue("app.secret")));
        jsonRequest.put("callbackUrl", notEncodedUrl); //after sign, need to pass not encoded url to source
        logger.info("Request to source with: {}", jsonRequest.toString());
        TransLogAccessor.getInstance().AddTransLog(originalRequest, jsonRequest.toString(), ConstData.ANTQUEEN_QUERYVIN_REQUEST);

        String url = PropertyUtil.readValue("source.url") + "/api/queryByVin";
        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,jsonRequest);
        return response.getEntity(JSONObject.class);
    }


    private boolean canQueryVin(Object requestObject) throws Exception {
        AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
        if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(originalRequest.getVin())) {
            return true;
        } else {
            originalRequest.setSign(null);
            originalRequest.setCallbackUrl(null);
            originalRequest.setPartnerId(PropertyUtil.readValue("app.key"));
            originalRequest.setSign(EncryptUtil.sign(originalRequest, PropertyUtil.readValue("app.secret")));

            logger.info("Request to source with: {}", originalRequest.toString());
            TransLogAccessor.getInstance().AddTransLog(JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class), originalRequest.toString(), ConstData.ANTQUEEN_CHECKVIN_REQUEST);

            String url = PropertyUtil.readValue("source.url") + "/api/checkVin";
            webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, originalRequest);

            AntResponse sourceResponse = response.getEntity(AntResponse.class);
            logger.info("Got response: {}", sourceResponse);
            TransLogAccessor.getInstance().AddTransLog(JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class), sourceResponse.toString(), ConstData.ANTQUEEN_CHECKVIN_RESPONSE);

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

}
