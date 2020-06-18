package com.chetiwen.rest.service.antqueen;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;

import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/api")
public class GetBalanceInterface {
    private static Logger logger = LoggerFactory.getLogger(GetBalanceInterface.class);

    @POST
    @Path("/get/balance")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Get Balance request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg","success");
            JSONObject data = new JSONObject();
            data.put("balance", UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getBalance());
            jsonObject.put("data", data);

            logger.info("return balance {}", jsonObject.toJSONString());
            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

}
