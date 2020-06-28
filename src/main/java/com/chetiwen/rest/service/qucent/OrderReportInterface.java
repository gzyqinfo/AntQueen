package com.chetiwen.rest.service.qucent;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.*;
import com.chetiwen.common.LogType;
import com.chetiwen.controll.Authentication;
import com.chetiwen.controll.DataConvertor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.DebitLog;
import com.chetiwen.db.model.Order;
import com.chetiwen.object.antqueen.AntRequest;
import com.chetiwen.object.antqueen.AntResponse;
import com.chetiwen.object.antqueen.OrderReportRepairDetail;
import com.chetiwen.object.antqueen.OrderReportResponse;
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
public class OrderReportInterface {
    private static Logger logger = LoggerFactory.getLogger(OrderReportInterface.class);
    private static Client restClient;
    private static WebResource webResource;
    static {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        restClient = Client.create(config);
    }

    @POST
    @Path("/getOrderReport")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(Object requestObject) throws Exception {
        logger.info("---------------------------------------------------------------------------------------------------");
        logger.info("Received Get Order Report request with : {}", requestObject);

        try {
            if (!Authentication.jsonSign(requestObject)) {
                AntResponse response = Authentication.genAntResponse(1001, "签名错误", logger);
                return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(response)).build();
            }

            AntRequest originalRequest = JSONObject.parseObject(JSONObject.toJSONString(requestObject), AntRequest.class);
            TransLogAccessor.getInstance().AddTransLog(originalRequest, JSONObject.toJSONString(requestObject), LogType.CLIENT_ORDERREP_REQUEST);

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

            if (OrderReportCache.getInstance().getOrderReportMap().containsKey(sourceOrderNo)) {
                logger.info("there is cached order");
                Order order = OrderReportCache.getInstance().getByKey(sourceOrderNo);
                if (order !=null) {
                    OrderReportResponse orderReport = DataConvertor.convertToAntQueenReport(JSONObject.parseObject(order.getResponseContent(), QucentOrderResponse.class));
                    resetOrderReport(originalRequest, orderReport);
                    logger.info("Return OK. {}", orderReport.toString());
                    return Response.status(Response.Status.OK).entity(orderReport.toString()).build();
                }
            }

            AntResponse response = Authentication.genAntResponse(300018, "订单查询中", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();

        } catch (Exception e) {
            logger.error("Error: {}",e.getMessage());
            AntResponse response = Authentication.genAntResponse(1107, "服务异常", logger);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(JSONObject.toJSONString(response)).build();
        } finally {
            logger.info("###################################################################################################");
        }
    }

    private void resetOrderReport(AntRequest originalRequest, OrderReportResponse orderReport) {
        orderReport.getData().setReportNo(originalRequest.getOrderId());
        orderReport.getData().setReportUrl("http://ctw.che9000.com/#/showOrder?orderNo="+originalRequest.getOrderId());
        orderReport.getData().setMakeReportDate(originalRequest.getTs());
        if (orderReport.getData().getNormalRepairRecords() != null) {
            for (OrderReportRepairDetail repairDetail : orderReport.getData().getNormalRepairRecords()) {
                repairDetail.setOther(EncryptUtil.replacePhoneNumber(repairDetail.getOther()));
                repairDetail.setContent(EncryptUtil.replacePhoneNumber(repairDetail.getContent()));
                repairDetail.setType(EncryptUtil.replacePhoneNumber(repairDetail.getType()));
                repairDetail.setMaterial(EncryptUtil.replacePhoneNumber(repairDetail.getMaterial()));
            }
        }
    }


}
