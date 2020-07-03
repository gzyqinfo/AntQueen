package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;

import com.chetiwen.cache.BrandCache;
import com.chetiwen.cache.DebitLogCache;
import com.chetiwen.cache.UserCache;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.UserAuditAccessor;
import com.chetiwen.db.model.Brand;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.UserAudit;
import com.chetiwen.object.BillDetail;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Path("/api")
public class FrontEndResource {
    private static Logger logger = LoggerFactory.getLogger(FrontEndResource.class);


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
            logger.info("###################################################################################################");
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
            logger.info("###################################################################################################");
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

            JSONObject jsonObject = generateReturnJson(originalRequest, JSONObject.toJSONString(dataList), dataList.size());
            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    @POST
    @Path("/bill/details")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response getUserBills(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received getUserBills request with : {}", JSONObject.toJSONString(requestObject));

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);

            List<UserAudit> userAuditList = UserAuditAccessor.getInstance().getUserAudit(originalRequest.getPartnerId());
            List<BillDetail> billDetailList = new ArrayList<>();
            String userName = UserCache.getInstance().getByKey(originalRequest.getPartnerId()).getUserName();
            for (UserAudit userAudit : userAuditList) {
                BillDetail billDetail = null;
                if (ConstData.SQL_ACTION_INSERT.equalsIgnoreCase(userAudit.getAction())) {
                    billDetail = new BillDetail();
                    billDetail.setUserId(originalRequest.getPartnerId());
                    billDetail.setUserName(userName);
                    billDetail.setBillType(ConstData.BILL_TYPE_CHARGE);
                    billDetail.setTimestamp(userAudit.getCreateTime());
                    billDetail.setAmount(Float.valueOf(userAudit.getBalance()));
                } else if (ConstData.SQL_ACTION_UPDATE.equalsIgnoreCase(userAudit.getAction())) {
                    float before = Float.valueOf(userAudit.getBalance().split("->")[0]);
                    float after = Float.valueOf(userAudit.getBalance().split("->")[1]);
                    if (after - before > 0.000001) {
                        billDetail = new BillDetail();
                        billDetail.setUserId(originalRequest.getPartnerId());
                        billDetail.setUserName(userName);
                        billDetail.setBillType(ConstData.BILL_TYPE_CHARGE);
                        billDetail.setAmount(after-before);
                        billDetail.setTimestamp(userAudit.getCreateTime());
                    } else if (before - after > 0.000001) {
                        billDetail = new BillDetail();
                        billDetail.setBillType(ConstData.BILL_TYPE_CONSUME);
                        billDetail.setUserId(originalRequest.getPartnerId());
                        billDetail.setUserName(userName);
                        billDetail.setAmount(before-after);
                        billDetail.setTimestamp(userAudit.getCreateTime());
                    }
                }
                if (billDetail != null) {
                    billDetailList.add(billDetail);
                }
            }

            JSONObject jsonObject = generateReturnJson(originalRequest, JSONObject.toJSONString(billDetailList), billDetailList.size());

            return Response.status(Response.Status.OK).entity(jsonObject.toJSONString()).build();
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private JSONObject generateReturnJson(AntRequest originalRequest, String s, int size) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "success");
        JSONObject data = new JSONObject();
        data.put("list", s);
        jsonObject.put("data", data);

        logger.info("return {} rows historical orders list for partner : {}", size, originalRequest.getPartnerId());
        return jsonObject;
    }
}
