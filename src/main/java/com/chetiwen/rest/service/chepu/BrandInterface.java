//package com.chetiwen.rest.service.chepu;
//
//import com.alibaba.fastjson.JSONObject;
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
//@Path("/ctw")
//public class BrandInterface {
//
//    private static Logger logger = LoggerFactory.getLogger(BrandInterface.class);
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
//    @Path("/getBrandName")
//    @Consumes("application/json")
//    @Produces("application/json;charset=UTF-8")
//    public Response processRequest(ClientRequest clientRequest) throws Exception {
//        logger.info("-------------------------------------------------------------------------------------------------------");
//        logger.info("Received Brand Name request with : {}", clientRequest);
//        String toSourceRequest = null;
//        String sourceResponse = null;
//        try {
//
//            if (!Authentication.authenticateTime(clientRequest.getTimestamp())) {
//                CarResponse response = Authentication.genCarResponse("40002", "timestamp非法或超时", logger);
//                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
//            } else if (!Authentication.authenticateSign(clientRequest.getSign(), clientRequest.getTimestamp(), clientRequest.getAppKey())) {
//                CarResponse response = Authentication.genCarResponse("40006", "签名不一致", logger);
//                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
//            } else {
//                String url = PropertyUtil.readValue("source.url") + "/order/getBrandName_v1";
//                webResource = restClient.resource(url);
//
//                JSONObject json = new JSONObject();
//                json.fluentPut("version", 1);
//                json.fluentPut("timestamp", clientRequest.getTimestamp());
//                json.fluentPut("ticket", clientRequest.getTicket());
//                json.fluentPut("appKey", PropertyUtil.readValue("app.key"));
//                json.fluentPut("appSecret", PropertyUtil.readValue("app.secret"));
//                JSONObject body = new JSONObject();
//                body.fluentPut("orderNo", clientRequest.getBody().getOrderNo());
//                json.fluentPut("body", body);
//                json.fluentPut("sign", EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));
//
//                toSourceRequest = json.toJSONString();
//                logger.info("request to url:{} with json:{}", url, toSourceRequest);
//                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
//                CarResponse carResponse = response.getEntity(CarResponse.class);
//                logger.info("get car response: {}", carResponse);
//                sourceResponse = JSONObject.toJSONString(carResponse);
//
//                if ("0".equals(carResponse.getCode())) {
//                    carResponse.getData().setAppName("品牌");
//                }
//                logger.info("finish processing and return ok");
//                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(carResponse)).build();
//            }
//        } finally {
//            //TODO:  create a new thread to handle inside DB stuff
//            try {
//                TransactionLog log = new TransactionLog();
//                log.setAppKey(clientRequest.getAppKey());
//                log.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
//                log.setTicket(clientRequest.getTicket());
//                log.setRequestContent(clientRequest.toString());
//                log.setLogType("original brandName request");
//                TransLogAccessor.getInstance().addLog(log);
//
//                if (toSourceRequest != null) {
//
//                    TransactionLog requestlog = new TransactionLog();
//                    requestlog.setAppKey(clientRequest.getAppKey());
//                    requestlog.setRequestContent(toSourceRequest);
//                    requestlog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
//                    requestlog.setTicket(clientRequest.getTicket());
//                    requestlog.setLogType("source brandName request");
//                    TransLogAccessor.getInstance().addLog(requestlog);
//
//                    TransactionLog responselog = new TransactionLog();
//                    responselog.setAppKey(clientRequest.getAppKey());
//                    responselog.setResponseContent(sourceResponse);
//                    responselog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
//                    responselog.setTicket(clientRequest.getTicket());
//                    responselog.setLogType("source brandName response");
//                    TransLogAccessor.getInstance().addLog(responselog);
//                }
//            } catch (Exception e) {
//                logger.error("Error while storing data. {}/ {}", e.getMessage(), e.getCause());
//            }
//        }
//    }
//
//}
