package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.model.User;
import com.chetiwen.object.CarResponse;
import com.chetiwen.object.ClientRequest;
import com.chetiwen.object.HistoricalOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/car")
public class DebitLogInterface {
    private static Logger logger = LoggerFactory.getLogger(DebitLogInterface.class);

    @POST
    @Path("/historical/orders")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response listAll(ClientRequest clientRequest) {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received debit log request with appKey {} ", clientRequest);
        try {
            if (!Authentication.authenticateTime(clientRequest.getTimestamp())) {
                CarResponse response = Authentication.genCarResponse("40002", "timestamp非法或超时", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else if (!Authentication.authenticateSign(clientRequest.getSign(), clientRequest.getTimestamp(), clientRequest.getAppKey())) {
                CarResponse response = Authentication.genCarResponse("40006", "签名不一致", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else {
                HistoricalOrders orders = new HistoricalOrders();
                orders.setCode("0");
                orders.setMsg("success");
                orders.setOrders(DebitLogAccessor.getInstance().getDebitOrdersByAppKey(clientRequest.getAppKey()));

                logger.info("returned data {}", orders.toString());
                return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(orders)).build();
            }
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/user/getBalance")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getUserBalance(ClientRequest clientRequest) {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Get User balance request with : {}", clientRequest);

        try {
            if (!Authentication.authenticateTime(clientRequest.getTimestamp())) {
                CarResponse response = Authentication.genCarResponse("40002", "timestamp非法或超时", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else if (!Authentication.authenticateSign(clientRequest.getSign(), clientRequest.getTimestamp(), clientRequest.getAppKey())) {
                CarResponse response = Authentication.genCarResponse("40006", "签名不一致", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else {
                UserCache.getInstance().refreshCache();
                User user = UserCache.getInstance().getByKey(clientRequest.getAppKey());

                CarResponse response = Authentication.genCarResponse("0", "用户余额为: "+user.getBalance(), logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }


}
