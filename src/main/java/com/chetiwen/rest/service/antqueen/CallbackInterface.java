package com.chetiwen.rest.service.antqueen;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.ConstData;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.CallbackProcessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.TransactionLog;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.AntResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/api")
public class CallbackInterface {
    private static Logger logger = LoggerFactory.getLogger(CallbackInterface.class);


    @POST
    @Path("/callback/antqueen")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response AntQueenCallback(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Callback request from AntQueen : {}", requestObject);
        TransactionLog log = new TransactionLog();
        log.setLogType(ConstData.ANTQUEEN_CALLBACK);
        log.setUserName("CALLBACK");
        log.setPartnerId("SYSTEM");
        log.setTransactionContent(requestObject.toString());
        TransLogAccessor.getInstance().addLog(log);

        try {
            AntOrderResponse orderResponse = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntOrderResponse.class);

            if (orderResponse.getCode() == 0) {
                logger.info("orderResponse.getData().getOrderId() is {}", orderResponse.getData().getOrderId());
                if (!GetOrderCache.getInstance().getGetOrderMap().containsKey(String.valueOf(orderResponse.getData().getOrderId()))) {
                    Order getOrder = new Order();
                    getOrder.setOrderNo(String.valueOf(orderResponse.getData().getOrderId()));
                    getOrder.setResponseContent(JSONObject.toJSONString(requestObject));
                    GetOrderCache.getInstance().addGetOrder(getOrder);
                }

                if (OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())) != null) {
                    new CallbackProcessor().callback(OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())).getUrl(),
                            OrderCallbackCache.getInstance().getByKey(String.valueOf(orderResponse.getData().getOrderId())).getOrderNo());
                }
            }

            AntResponse response = Authentication.genAntResponse(200, "success", logger);
            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();


        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
            AntResponse response = Authentication.genAntResponse(99999, "接收回调处理异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }
}
