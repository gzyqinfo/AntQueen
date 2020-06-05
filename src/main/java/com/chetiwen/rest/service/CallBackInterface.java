package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.GetOrderCache;
import com.chetiwen.db.model.Order;
import com.chetiwen.object.CarResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/callback")
public class CallBackInterface {
    private static Logger logger = LoggerFactory.getLogger(CallBackInterface.class);

    @POST
    @Path("/order/get")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(CarResponse orderDetail) throws Exception {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Call Back request with : {}", orderDetail);

        if ("0".equals(orderDetail.getCode())) {
            if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(orderDetail.getData().getOrderNo())){
                Order getOrder = new Order();
                getOrder.setVin(orderDetail.getData().getResult().get(0).getVin());
                getOrder.setOrderNo(orderDetail.getData().getOrderNo());
                getOrder.setResponseContent(JSONObject.toJSONString(orderDetail));

                GetOrderCache.getInstance().addGetOrder(getOrder);
            }
        }

        return Response.status(Response.Status.OK).entity("ok").build();
    }


}
