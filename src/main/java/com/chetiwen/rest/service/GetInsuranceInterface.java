package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.TransactionLog;
import com.chetiwen.object.CarResponse;
import com.chetiwen.object.ClientRequest;
import com.chetiwen.object.PackBody;
import com.chetiwen.util.EncryptUtil;
import com.chetiwen.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/car")
public class GetInsuranceInterface {
    private static Logger logger = LoggerFactory.getLogger(GetInsuranceInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    @POST
    @Path("/insurance/getOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(ClientRequest clientRequest) throws Exception {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Get Insurance request with : {}", clientRequest);
        String toSourceRequest = null;
        String sourceResponse = null;
        try {

            if (restClient == null) {
                ClientConfig config = new DefaultClientConfig();
                config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                restClient = Client.create(config);
            }

            if (!Authentication.authenticateTime(clientRequest.getTimestamp())) {
                CarResponse response = Authentication.genCarResponse("40002", "timestamp非法或超时", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else if (!Authentication.authenticateSign(clientRequest.getSign(), clientRequest.getTimestamp(), clientRequest.getAppKey())) {
                CarResponse response = Authentication.genCarResponse("40006", "签名不一致", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else {
                String url = PropertyUtil.readValue("source.url") + "/insurance/getOrderResult_v1";
                webResource = restClient.resource(url);

                JSONObject json = new JSONObject();
                json.fluentPut("version", 1);
                json.fluentPut("timestamp", clientRequest.getTimestamp());
                json.fluentPut("ticket", clientRequest.getTicket());
                json.fluentPut("appKey", PropertyUtil.readValue("app.key.insurance"));
                json.fluentPut("appSecret", PropertyUtil.readValue("app.secret.insurance"));
                JSONObject body = new JSONObject();
                body.fluentPut("orderNo", clientRequest.getBody().getOrderNo());
                json.fluentPut("body", body);
                json.fluentPut("sign", EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));

                toSourceRequest = json.toJSONString();
                logger.info("request to url:{} with json:{}", url, toSourceRequest);
                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);

                sourceResponse = response.getEntity(Object.class).toString();
                logger.info("got response: {}", sourceResponse);

                logger.info("finish processing and return ok");
                return Response.status(Response.Status.OK).entity(sourceResponse).build();
            }
        } finally {
            //TODO:  create a new thread to handle inside DB stuff
            try {
                TransactionLog log = new TransactionLog();
                log.setAppKey(clientRequest.getAppKey());
                log.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                log.setTicket(clientRequest.getTicket());
                log.setRequestContent(clientRequest.toString());
                log.setLogType("original getInsurance request");
                TransLogAccessor.getInstance().addLog(log);

                if (toSourceRequest != null) {

                    TransactionLog requestlog = new TransactionLog();
                    requestlog.setAppKey(clientRequest.getAppKey());
                    requestlog.setRequestContent(toSourceRequest);
                    requestlog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    requestlog.setTicket(clientRequest.getTicket());
                    requestlog.setLogType("source getInsurance request");
                    TransLogAccessor.getInstance().addLog(requestlog);

                    TransactionLog responselog = new TransactionLog();
                    responselog.setAppKey(clientRequest.getAppKey());
                    responselog.setResponseContent(sourceResponse);
                    responselog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    responselog.setTicket(clientRequest.getTicket());
                    responselog.setLogType("source getInsurance response");
                    TransLogAccessor.getInstance().addLog(responselog);
                }
            } catch (Exception e) {
                logger.error("Error while storing data. {}/ {}", e.getMessage(), e.getCause());
            }
        }
    }


}
