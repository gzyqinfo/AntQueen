package com.chetiwen.rest.service.qucent;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.LogType;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.Order;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.AntOrderResult;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
import com.chetiwen.object.qucent.QucentOrderResponse;
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


@Path("/api/ctw")
public class GetOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(GetOrderInterface.class);

    @POST
    @Path("/getOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Get Order request with : {}", requestObject);

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), LogType.CLIENT_GETORDER_REQUEST);

            String sourceOrderNo = originalRequest.getOrderId();
            if (!DebitLogCache.getInstance().getDebitLogMap().containsKey(originalRequest.getPartnerId()+"/"+sourceOrderNo)) {
                logger.info("No debit record for {} with order : {}", originalRequest.getPartnerId(), sourceOrderNo);
                AntResponse response = Authentication.genAntResponse(1200, "无效订单号", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            if (OrderMapCache.getInstance().getOrderMap().containsKey(originalRequest.getOrderId())) {
                logger.info("there is replaced order");
                sourceOrderNo = OrderMapCache.getInstance().getByKey(originalRequest.getOrderId()).getOrderNo();
            }

            if (GetOrderCache.getInstance().getGetOrderMap().containsKey(sourceOrderNo)) {
                logger.info("there is cached order");
                Order order = GetOrderCache.getInstance().getByKey(sourceOrderNo);
                if (order !=null) {
                    AntOrderResponse orderResponse = DataConvertor.convertToAntQueenOrder(JSONObject.parseObject(order.getResponseContent(), QucentOrderResponse.class));
                    orderResponse.getData().setOrderId(originalRequest.getOrderId());
                    orderResponse.getData().setMobilUrl(null);
                    orderResponse.getData().setPcUrl(null);
                    orderResponse.getData().setPcUrl("http://ctw.che9000.com/#/showOrder?orderNo="+orderResponse.getData().getOrderId());
                    replacePhoneNumber(orderResponse);
                    logger.info("Return OK. {}", orderResponse.toString());
                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(orderResponse)).build();
                }
            }
            AntResponse response = Authentication.genAntResponse(300018, "订单查询中", logger);
            return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();

        }  catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private void replacePhoneNumber(AntOrderResponse orderResponse) {
        if (orderResponse.getData().getRecords() != null) {
            for (AntOrderResult orderResult : orderResponse.getData().getRecords()) {
                if (orderResult.getOther() != null) {
                    orderResult.setOther(EncryptUtil.replacePhoneNumber(orderResult.getOther()));
                    orderResult.setContent(EncryptUtil.replacePhoneNumber(orderResult.getContent()));
                    orderResult.setMaterial(EncryptUtil.replacePhoneNumber(orderResult.getMaterial()));
                    orderResult.setType(EncryptUtil.replacePhoneNumber(orderResult.getType()));
                }
            }
        }
    }

}
