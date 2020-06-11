package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSON;
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
public class CallbackInterface {
    private static Logger logger = LoggerFactory.getLogger(CallbackInterface.class);
    private static Client restClient;
    private static WebResource webResource;
    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/callback")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Callback request with : {}", requestObject);

        try {
            AntOrderResponse orderResponse = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntOrderResponse.class);

            if (orderResponse.getCode() == 0) {
                if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(orderResponse.getData().getOrderId())){
                    Order getOrder = new Order();
                    getOrder.setOrderNo(String.valueOf(orderResponse.getData().getOrderId()));
                    getOrder.setResponseContent(JSONObject.toJSONString(requestObject));
                    GetOrderCache.getInstance().addGetOrder(getOrder);
                }
            }

            AntResponse response = Authentication.genAntResponse(200, "success", logger);
            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();


        }  catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(99999, "接收回调处理异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }


}
