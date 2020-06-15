package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;

import com.chetiwen.cache.BrandCache;
import com.chetiwen.cache.DebitLogCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.Brand;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.object.AntRequest;
import com.chetiwen.object.AntResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@Path("/api")
public class FrontEndResource {
    private static Logger logger = LoggerFactory.getLogger(FrontEndResource.class);

    @GET
    @Path("/myBandlist")
    @Produces("application/json;charset=UTF-8")
    public Response listAll() {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Brand list request ");
        try {
            List<Brand> dataList = BrandCache.getInstance().getBrandMap().values()
                    .stream().collect(Collectors.toList());

            logger.info("returned {} row(s) data", dataList.size());
            return Response.status(Response.Status.OK).entity(new JSONObject().toJSONString(dataList)).build();
        } catch (DBAccessException e) {
            logger.error("Fail to connect to DataBase, error: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/authentication")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response authentication(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received User authentication request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg","success");

            logger.info("return {}", jsonObject.toJSONString());
            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("===================================================================================================");
        }
    }

    @POST
    @Path("/brand/list")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getBrands(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Brand List request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            List<Brand> dataList = BrandCache.getInstance().getBrandMap().values()
                    .stream().collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg","success");
            JSONObject data = new JSONObject();
            data.put("list", JSONObject.toJSONString(dataList));
            jsonObject.put("data", data);

            logger.info("return brand list {}", jsonObject.toJSONString());
            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("===================================================================================================");
        }
    }

    @POST
    @Path("/historical/orders")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getHistoricalOrders(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received getHistoricalOrders request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);

            List<DebitLog> dataList = DebitLogCache.getInstance().getDebitLogMap().values()
                    .stream().filter(debitLog->debitLog.getPartnerId().equals(originalRequest.getPartnerId()))
                    .collect(Collectors.toList());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("msg","success");
            JSONObject data = new JSONObject();
            data.put("list", JSONObject.toJSONString(dataList));
            jsonObject.put("data", data);

            logger.info("return historical orders list for partner : {}", originalRequest.getPartnerId());
            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("===================================================================================================");
        }
    }
}
