package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.model.*;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
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

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    private String antQueenUrl = "http://localhost:"+PropertyUtil.readValue("app.port")+"/api/saveOrder";
    private String qucentUrl = "http://localhost:"+PropertyUtil.readValue("app.port")+"/api/b2b/saveOrder";

    @POST
    @Path("/queryByVin")
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

            User user = UserCache.getInstance().getByKey(originalRequest.getPartnerId());
            if (user.getDataSource().toUpperCase().contains(ConstData.DATA_SOURCE_ALL)) {
                ClientResponse response = redirectTo(requestObject, antQueenUrl);
                JSONObject jsonObject = response.getEntity(JSONObject.class);
                if (!String.valueOf(jsonObject.get("code").toString()).equals("0")) {
                    response = redirectTo(requestObject, qucentUrl);
                    jsonObject = response.getEntity(JSONObject.class);
                }
                return Response.status(response.getStatus()).entity(jsonObject).build();
            } else if (user.getDataSource().toUpperCase().contains(ConstData.DATA_SOURCE_ANTQUEEN)) {
                ClientResponse response = redirectTo(requestObject, antQueenUrl);
                return Response.status(response.getStatus()).entity(response.getEntity(String.class)).build();
            } else if (user.getDataSource().toUpperCase().contains(ConstData.DATA_SOURCE_QUCENT)) {
                ClientResponse response = redirectTo(requestObject, qucentUrl);
                return Response.status(response.getStatus()).entity(response.getEntity(String.class)).build();
            } else {
                throw new RuntimeException();
            }

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private ClientResponse redirectTo(Object requestObject, String url) {
        webResource = restClient.resource(url);
        return webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestObject);
    }

}
