package com.chetiwen.rest.service.ai.robot;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ai")
public class ChatInterface {
    private static Logger logger = LoggerFactory.getLogger(ChatInterface.class);
    // 请求url
    private String REQUEST_URL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

    private static Client restClient;
    private static WebResource webResource;

    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @GET
    @Path("/robot/chat/{msg}")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(@PathParam("msg") String message) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Robot chat request message: {}.", message);
        long timeStart = System.currentTimeMillis();
        REQUEST_URL+=message;
        String result;
        try {
            webResource = restClient.resource(REQUEST_URL);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
            result = response.getEntity(String.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error msg: ", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        logger.info("Returned : {}", result);
        logger.info("Time used: {} ms", (System.currentTimeMillis() - timeStart));
        logger.info("###################################################################################################");
        return Response.status(Response.Status.OK).entity(result).build();

    }
}
