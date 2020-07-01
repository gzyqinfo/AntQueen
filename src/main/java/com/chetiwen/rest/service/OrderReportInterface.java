package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.OrderMap;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
import com.chetiwen.object.antqueen.OrderReportRepairDetail;
import com.chetiwen.object.antqueen.OrderReportResponse;
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
public class OrderReportInterface {
    private static Logger logger = LoggerFactory.getLogger(OrderReportInterface.class);
    private static Client restClient;
    private static WebResource webResource;
    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/getOrderReport")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Get Order Report request with : {}", requestObject);

        try {
//            if (!Authentication.jsonSign(requestObject)) {
//                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
//                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
//            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), ConstData.CLIENT_ORDERREP_REQUEST);

            String sourceOrderNo = originalRequest.getOrderId();
//            if (!DebitLogCache.getInstance().getDebitLogMap().containsKey(originalRequest.getPartnerId()+"/"+sourceOrderNo)) {
//                logger.info("No debit record for {} with order : {}", originalRequest.getPartnerId(), sourceOrderNo);
//                AntResponse response = Authentication.genAntResponse(1200, "无效订单号", logger);
//                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
//            }

            if (OrderMapCache.getInstance().getOrderMap().containsKey(originalRequest.getOrderId())) {
                logger.info("there is replaced order");
                sourceOrderNo = OrderMapCache.getInstance().getByKey(originalRequest.getOrderId()).getOrderNo();
            }

            if (OrderReportCache.getInstance().getOrderReportMap().containsKey(sourceOrderNo)) {
                logger.info("there is cached order");
                Order order = OrderReportCache.getInstance().getByKey(sourceOrderNo);
                if (order !=null) {
                    OrderReportResponse orderReport = JSONObject.parseObject(order.getResponseContent(), OrderReportResponse.class);
                    resetOrderReport(originalRequest, orderReport);
                    logger.info("Return OK. {}", orderReport.toString());
                    return Response.status(Response.Status.OK).entity(orderReport.toString()).build();
                }
            }

            if (sourceOrderNo.length() > 30) { // Order from Qucent
                logger.info("source order no : {}", sourceOrderNo);
                AntResponse response = Authentication.genAntResponse(1102, "订单查询中", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            JSONObject antResponse = askSource(requestObject, originalRequest, sourceOrderNo);

            logger.info("get ant response: {}", antResponse.toJSONString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, antResponse.toJSONString(), ConstData.ANTQUEEN_ORDERREP_RESPONSE);

            if ("0".equals(antResponse.get("code").toString())) {
                OrderReportResponse orderReport = JSONObject.parseObject(antResponse.toJSONString(), OrderReportResponse.class);
                resetOrderReport(originalRequest, orderReport);

                if (!OrderReportCache.getInstance().getOrderReportMap().containsKey(sourceOrderNo)){
                    Order getOrder = new Order();
                    getOrder.setOrderNo(sourceOrderNo);
                    getOrder.setVin(String.valueOf(orderReport.getData().getVin()));
                    getOrder.setResponseContent(antResponse.toJSONString());
                    OrderReportCache.getInstance().addOrderReport(getOrder);
                }
                logger.info("finish processing and return ok. {}", orderReport.toString());
                return Response.status(Response.Status.OK).entity(orderReport.toString()).build();
            } else if (!"1102".equals(antResponse.get("code").toString())) {//一个订单, 除了查询中的状态(code:1102) 其它状态不会再改动
                //对已收款退费，同时不再支持该订单的查询
                String partnerId = originalRequest.getPartnerId();
                String debitKey = partnerId+"/"+originalRequest.getOrderId();;
                DebitLog debitLog = DebitLogCache.getInstance().getDebitLogMap().get(debitKey);
                if (debitLog != null) {
                    float debitFee = debitLog.getDebitFee();
                    UserCache.getInstance().getByKey(partnerId).setBalance(UserCache.getInstance().getByKey(partnerId).getBalance()+debitFee);
                    UserCache.getInstance().updateUser(UserCache.getInstance().getByKey(partnerId));
                    logger.info("Add debitFee :{} back to user {}'s balance", debitFee, partnerId);

                    DebitLogCache.getInstance().delDebitLog(debitKey);
                    OrderMap orderMap = OrderMapCache.getInstance().getByKey(debitKey.split("/")[1]);
                    if (orderMap != null) {
                        SaveOrderCache.getInstance().delSaveOrder(orderMap.getOrderNo());
                    }
                }
            }
            return Response.status(Response.Status.OK).entity(antResponse.toJSONString()).build();

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private void resetOrderReport(AntRequest originalRequest, OrderReportResponse orderReport) {
        orderReport.getData().setReportNo(originalRequest.getOrderId());
        orderReport.getData().setReportUrl("http://ctw.che9000.com/#/showOrder?orderNo="+originalRequest.getOrderId());
        orderReport.getData().setMakeReportDate(originalRequest.getTs());
        if (orderReport.getData().getNormalRepairRecords() != null) {
            for (OrderReportRepairDetail repairDetail : orderReport.getData().getNormalRepairRecords()) {
                repairDetail.setOther(EncryptUtil.replacePhoneNumber(repairDetail.getOther()));
                repairDetail.setContent(EncryptUtil.replacePhoneNumber(repairDetail.getContent()));
                repairDetail.setType(EncryptUtil.replacePhoneNumber(repairDetail.getType()));
                repairDetail.setMaterial(EncryptUtil.replacePhoneNumber(repairDetail.getMaterial()));
            }
        }
    }

    private JSONObject askSource(Object requestObject, AntRequest originalRequest, String sourceOrderNo) throws Exception {
        JSONObject jsonRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject));
        jsonRequest.put("partnerId", PropertyUtil.readValue("app.key"));
        jsonRequest.put("orderId", Integer.valueOf(sourceOrderNo));
        jsonRequest.remove("sign");
        jsonRequest.put("sign", EncryptUtil.sign(jsonRequest.toJSONString(), PropertyUtil.readValue("app.secret")));

        logger.info("Request to source with: {}", jsonRequest.toString());
        TransLogAccessor.getInstance().AddTransLog(originalRequest, jsonRequest.toString(), ConstData.ANTQUEEN_ORDERREP_REQUEST);

        String url = PropertyUtil.readValue("source.url") + "/api/getReportDetectData";
        webResource = restClient.resource(url);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,jsonRequest);
        return response.getEntity(JSONObject.class);
    }


}
