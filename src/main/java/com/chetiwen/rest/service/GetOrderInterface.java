package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.Order;
import com.chetiwen.object.AntOrderResponse;
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
public class GetOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(GetOrderInterface.class);
    private static Client restClient;
    private static WebResource webResource;
    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/getOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Get Order request with : {}", requestObject);

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original getOrder request");

            String sourceOrderNo = originalRequest.getOrderId();
            if (OrderMapCache.getInstance().getOrderMap().containsKey(originalRequest.getOrderId())) {
                logger.info("there is replaced order");
                sourceOrderNo = OrderMapCache.getInstance().getByKey(originalRequest.getOrderId()).getOrderNo();
            }

            if (!DebitLogCache.getInstance().getDebitLogMap().containsKey(originalRequest.getPartnerId()+"/"+sourceOrderNo)) {
                logger.info("No debit record for {} with order : {}", originalRequest.getPartnerId(), sourceOrderNo);
                AntResponse response = Authentication.genAntResponse(1200, "无效订单号", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            if (GetOrderCache.getInstance().getGetOrderMap().containsKey(sourceOrderNo)) {
                logger.info("there is cached order");
                Order order = GetOrderCache.getInstance().getByKey(sourceOrderNo);
                if (order !=null) {
                    AntOrderResponse orderResponse = JSONObject.parseObject(order.getResponseContent(), AntOrderResponse.class);
                    orderResponse.getData().setMobilUrl(null);
                    orderResponse.getData().setPcUrl(null); //TODO reset url
                    logger.info("Return OK. {}", orderResponse.toString());
                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(orderResponse)).build();
                }
            }

            JSONObject jsonRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject));

            jsonRequest.put("partnerId", PropertyUtil.readValue("app.key"));
            jsonRequest.remove("sign");
            jsonRequest.put("sign", EncryptUtil.getAntSign(jsonRequest.toJSONString(), PropertyUtil.readValue("app.secret")));

            logger.info("Request to source with: {}", jsonRequest.toString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, jsonRequest.toString(), "source getOrder request");

            String url = PropertyUtil.readValue("source.url") + "/api/getOrderInfo";
            webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,jsonRequest);
            JSONObject antResponse = response.getEntity(JSONObject.class);

            logger.info("get ant response: {}", antResponse.toJSONString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, antResponse.toJSONString(), "source getOrder response");

            if ("0".equals(antResponse.get("code").toString())) {
                if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(sourceOrderNo)){
                    Order getOrder = new Order();
                    getOrder.setOrderNo(sourceOrderNo);
                    getOrder.setResponseContent(antResponse.toJSONString());
                    GetOrderCache.getInstance().addGetOrder(getOrder);

                }
                AntOrderResponse orderResponse = JSONObject.parseObject(antResponse.toJSONString(), AntOrderResponse.class);
                orderResponse.getData().setMobilUrl(null);
                orderResponse.getData().setPcUrl(null); //TODO reset url
                logger.info("finish processing and return ok. {}", JSONObject.toJSONString(orderResponse));
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(orderResponse)).build();
            } else if (!"1102".equals(antResponse.get("code").toString())) {//一个订单, 除了查询中的状态(code:1102) 其它状态不会再改动
                //对已收款退费，同时不再支持该订单的查询
                String partnerId = originalRequest.getPartnerId();
                String debitKey = partnerId+"/"+sourceOrderNo;
                DebitLog debitLog = DebitLogCache.getInstance().getDebitLogMap().get(debitKey);
                if (debitLog == null) {
                    debitKey = partnerId+"/"+originalRequest.getOrderId();
                    debitLog = DebitLogCache.getInstance().getDebitLogMap().get(debitKey);
                }
                if (debitLog != null) {
                    float debitFee = debitLog.getDebitFee();
                    UserCache.getInstance().getByKey(partnerId).setBalance(UserCache.getInstance().getByKey(partnerId).getBalance()+debitFee);
                    UserCache.getInstance().updateUser(UserCache.getInstance().getByKey(partnerId));
                    logger.info("Add debitFee :{} back to user {}'s balance", debitFee, partnerId);

                    DebitLogCache.getInstance().delDebitLog(debitKey);
                    SaveOrderCache.getInstance().delSaveOrder(debitKey.split("/")[1]);
                }
            }
            return Response.status(Response.Status.OK).entity(antResponse.toJSONString()).build();

        }  catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }


}
