package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.DebitLogAccessor;
import com.chetiwen.db.accesser.GetOrderAccessor;
import com.chetiwen.db.accesser.TransLogAccessor;
import com.chetiwen.db.model.*;
import com.chetiwen.object.CarResponse;
import com.chetiwen.object.ClientRequest;
import com.chetiwen.object.PackBody;
import com.chetiwen.util.EncryptUtil;
import com.chetiwen.util.PropertyUtil;
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


@Path("/car")
public class SaveInsuranceInterface {
    private static final float DEFAUT_FEE = 45f;
    private static Logger logger = LoggerFactory.getLogger(SaveInsuranceInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    @POST
    @Path("/insurance/saveOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(ClientRequest clientRequest) throws Exception {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Save Insurance Order request with : {}", clientRequest);
        String toSourceRequest = null;
        String sourceResponse = null;
        try {

            if (restClient == null) {
                ClientConfig config = new DefaultClientConfig();
                config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                restClient = Client.create(config);
            }

            if (!Authentication.authenticateTime(clientRequest.getTimestamp())) {
                CarResponse response = Authentication.genCarResponse("40002", "timestamp非法或超时", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else if (!Authentication.authenticateSign(clientRequest.getSign(), clientRequest.getTimestamp(), clientRequest.getAppKey())) {
                CarResponse response = Authentication.genCarResponse("40006", "签名不一致", logger);
                return Response.status(Response.Status.BAD_REQUEST).entity(JSONObject.toJSONString(response)).build();
            } else {
                float balanceBeforeDebit = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getBalance();

                if (balanceBeforeDebit - DEFAUT_FEE < 0.00000001) {
                    CarResponse badResponse =  Authentication.genCarResponse("400015", "余额不足，请充值!!", logger);
                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
                }

                String url = PropertyUtil.readValue("source.url") + "/insurance/saveOrder_v1";
                webResource = restClient.resource(url);

                JSONObject json = new JSONObject();
                json.fluentPut("version", 1);
                json.fluentPut("timestamp", clientRequest.getTimestamp());
                json.fluentPut("ticket", clientRequest.getTicket());
                json.fluentPut("appKey", PropertyUtil.readValue("app.key.insurance"));
                json.fluentPut("appSecret", PropertyUtil.readValue("app.secret.insurance"));
                JSONObject body = new JSONObject();
                body.fluentPut("vin", clientRequest.getBody().getVin());
                if (clientRequest.getBody().getBrandId() != null) {
                    body.fluentPut("brandId", clientRequest.getBody().getBrandId());
                }
                if (clientRequest.getBody().getEnginNum() != null) {
                    body.fluentPut("enginNum", clientRequest.getBody().getEnginNum());
                }
                body.fluentPut("callBackUrl", clientRequest.getBody().getCallBackUrl());
                json.fluentPut("body", body);
                json.fluentPut("sign", EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));

                toSourceRequest = json.toJSONString();
                logger.info("request to url:{} with json:{}", url, toSourceRequest);
                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);

                sourceResponse = response.getEntity(Object.class).toString();
                logger.info("got response: {}", sourceResponse);

                if (sourceResponse.indexOf("code=0") != -1) {
                    //debit
                    DebitLog debitLog = new DebitLog();
                    debitLog.setAppKey(clientRequest.getAppKey());
                    debitLog.setBalanceBeforeDebit(balanceBeforeDebit);
                    debitLog.setDebitFee(DEFAUT_FEE);
//                    debitLog.setOrderNo(response.getData().getOrderNo());
                    debitLog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());

                    DebitLogAccessor.getInstance().addLog(debitLog);
                    User updatedUser = UserCache.getInstance().getByKey(clientRequest.getAppKey());
                    updatedUser.setBalance(balanceBeforeDebit - DEFAUT_FEE);
                    UserCache.getInstance().updateUser(updatedUser);
                }
                logger.info("finish processing and return ok");
                return Response.status(Response.Status.OK).entity(sourceResponse).build();
            }
        } finally {
            //TODO:  create a new thread to handle inside DB stuff
            try {
                TransactionLog log = new TransactionLog();
                log.setAppKey(clientRequest.getAppKey());
                log.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                log.setTicket(clientRequest.getTicket());
                log.setRequestContent(clientRequest.toString());
                log.setLogType("original saveInsurance request");
                TransLogAccessor.getInstance().addLog(log);

                if (toSourceRequest != null) {

                    TransactionLog requestlog = new TransactionLog();
                    requestlog.setAppKey(clientRequest.getAppKey());
                    requestlog.setRequestContent(toSourceRequest);
                    requestlog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    requestlog.setTicket(clientRequest.getTicket());
                    requestlog.setLogType("source saveInsurance request");
                    TransLogAccessor.getInstance().addLog(requestlog);

                    TransactionLog responselog = new TransactionLog();
                    responselog.setAppKey(clientRequest.getAppKey());
                    responselog.setResponseContent(sourceResponse);
                    responselog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    responselog.setTicket(clientRequest.getTicket());
                    responselog.setLogType("source saveInsurance response");
                    TransLogAccessor.getInstance().addLog(responselog);
                }
            } catch (Exception e) {
                logger.error("Error while storing data. {}/ {}", e.getMessage(), e.getCause());
            }
        }
    }

    private void callBack(ClientRequest clientRequest) {

        String url = clientRequest.getBody().getCallBackUrl();
        webResource = restClient.resource(url);

        Order getOrder = null;
        try {
            getOrder = GetOrderAccessor.getInstance().getOrderByVin(clientRequest.getBody().getVin());
        } catch (DBAccessException e) {
            logger.error("call back error. {}", e.getMessage());
            e.printStackTrace();
        }

        if (getOrder !=null) {
            logger.info("got from cache, reset order No");
            CarResponse carResponse = JSONObject.parseObject(getOrder.getResponseContent(), CarResponse.class);
            carResponse.getData().setOrderNo(clientRequest.getBody().getOrderNo());
            webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, JSONObject.toJSONString(carResponse));
        }

    }

    private String generateOrderNo() {
        Date date = new Date();
        String strDateFormat = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        String datePrefix =sdf.format(date);


        return datePrefix+"000" + System.currentTimeMillis();
    }

}
