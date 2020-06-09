//package com.chetiwen.rest.service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chetiwen.cache.OrderMapCache;
//import com.chetiwen.controll.Authentication;
//import com.chetiwen.db.accesser.TransLogAccessor;
//import com.chetiwen.db.model.Order;
//import com.chetiwen.object.AntRequest;
//import com.chetiwen.object.AntResponse;
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.api.client.config.ClientConfig;
//import com.sun.jersey.api.client.config.DefaultClientConfig;
//import com.sun.jersey.api.json.JSONConfiguration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//@Path("/api")
//public class GetOrderInterface {
//    private static Logger logger = LoggerFactory.getLogger(GetOrderInterface.class);
//    private static Client restClient;
//    private static WebResource webResource;
//
//    static {
//        ClientConfig config = new DefaultClientConfig();
//        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
//        restClient = Client.create(config);
//    }
//
//    @POST
//    @Path("/getOrder")
//    @Consumes("application/json")
//    @Produces("application/json;charset=UTF-8")
//    public Response processRequest(Object requestObject) {
//        logger.info("---------------------------------------------------------------------------------------------------");
//        logger.info("Received Get Order request with : {}", JSONObject.toJSONString(requestObject));
//
//        try {
//            if (!Authentication.jsonSign(requestObject)) {
//                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
//                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
//            }
//
//            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
//            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original getOrder request");
//
//            String sourceOrderNo = originalRequest.getOrderId();
//            if (OrderMapCache.getInstance().getOrderMap().containsKey(originalRequest.getOrderId())) {
//                logger.info("there is cached order");
//                sourceOrderNo = OrderMapCache.getInstance().getByKey(originalRequest.getOrderId()).getOrderNo();
//                Order order = GetOrderAccessor.getInstance().getOrderByOrderNo(sourceOrderNo);
//
//                if (order != null) {
//                    logger.info("got from cache, reset order No");
//                    CarResponse carResponse = JSONObject.parseObject(order.getResponseContent(), CarResponse.class);
//                    carResponse.getData().setOrderNo(clientRequest.getBody().getOrderNo());
//                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(carResponse)).build();
//                }
//            } else if (GetOrderCache.getInstance().getGetOrderMap().containsKey(clientRequest.getBody().getOrderNo())) {
//                logger.info("there is cached order");
//                Order order = GetOrderAccessor.getInstance().getOrderByOrderNo(clientRequest.getBody().getOrderNo());
//                if (order != null) {
//                    logger.info("got from cache, no need to reset order No");
//                    CarResponse carResponse = JSONObject.parseObject(order.getResponseContent(), CarResponse.class);
//                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(carResponse)).build();
//                }
//            }
//
//            String url = PropertyUtil.readValue("source.url") + "/order/getOrderResult_v1";
//            webResource = restClient.resource(url);
//
//            JSONObject json = new JSONObject();
//            json.fluentPut("version", 1);
//            json.fluentPut("timestamp", clientRequest.getTimestamp());
//            json.fluentPut("ticket", clientRequest.getTicket());
//            json.fluentPut("appKey", PropertyUtil.readValue("app.key"));
//            json.fluentPut("appSecret", PropertyUtil.readValue("app.secret"));
//            JSONObject body = new JSONObject();
//            body.fluentPut("orderNo", sourceOrderNo);
//            json.fluentPut("body", body);
//            json.fluentPut("sign", EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));
//
//            toSourceRequest = json.toJSONString();
//            logger.info("request to url:{} with json:{}", url, toSourceRequest);
//            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
//            CarResponse carResponse = response.getEntity(CarResponse.class);
//            logger.info("get car response: {}", carResponse);
//            sourceResponse = JSONObject.toJSONString(carResponse);
//            ;
//
//            if ("0".equals(carResponse.getCode())) {
//                if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(carResponse.getData().getOrderNo())) {
//                    Order getOrder = new Order();
//                    getOrder.setVin(carResponse.getData().getResult() == null ? null : carResponse.getData().getResult().get(0).getVin());
//                    getOrder.setOrderNo(carResponse.getData().getOrderNo());
//                    getOrder.setResponseContent(JSONObject.toJSONString(carResponse));
//
//                    GetOrderCache.getInstance().addGetOrder(getOrder);
//                }
//
//            }
//            logger.info("finish processing and return ok");
//            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(carResponse)).build();
//        } catch (Exception e) {
//            logger.error("Error: {}", e.getMessage());
//            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
//        } finally {
//            logger.info("###################################################################################################");
//        }
//    }
//}
