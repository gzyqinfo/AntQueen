package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.BrandCache;
import com.chetiwen.cache.OrderMapCache;
import com.chetiwen.cache.SaveOrderCache;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.accesser.GetOrderAccessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.*;
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
import java.text.SimpleDateFormat;
import java.util.Date;


@Path("/api")
public class CheckVinInterface {
    private static Logger logger = LoggerFactory.getLogger(CheckVinInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    @POST
    @Path("/checkVin")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) throws Exception {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Check Vin request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (restClient == null) {
                ClientConfig config = new DefaultClientConfig();
                config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                restClient = Client.create(config);
            }

            if (!Authentication.authenticateMD5Sign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            }
            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), "original checkVin request");

            AntRequest antRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            antRequest.setSign(null);
            antRequest.setPartnerId(PropertyUtil.readValue("app.key"));
            antRequest.setSign(EncryptUtil.getAntSign(antRequest, PropertyUtil.readValue("app.secret")));

            logger.info("Request to source with: {}", antRequest.toString());
            TransLogAccessor.getInstance().AddTransLog(originalRequest, antRequest.toString(), "source checkVin request");

            String url = PropertyUtil.readValue("source.url") + "/api/checkVin";
            webResource = restClient.resource(url);
            ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,antRequest);

            String sourceResponse = response.getEntity(Object.class).toString();
            logger.info("Got response: {}", sourceResponse);

            TransLogAccessor.getInstance().AddTransLog(originalRequest, sourceResponse, "source checkVin response");
            logger.info("return OK");
            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(sourceResponse)).build();
        } catch (Exception e) {
            logger.error("Error while storing data. {}/ {}", e.getMessage(), e.getCause());
            return null;
        }
    }




}
