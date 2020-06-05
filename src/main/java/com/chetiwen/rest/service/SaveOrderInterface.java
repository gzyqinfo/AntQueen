package com.chetiwen.rest.service;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.chetiwen.cache.BrandCache;
import com.chetiwen.cache.OrderMapCache;
import com.chetiwen.cache.SaveOrderCache;
import com.chetiwen.cache.UserCache;
import com.chetiwen.controll.Authentication;
import com.chetiwen.db.ConnectionPool;
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
public class SaveOrderInterface {
    private static Logger logger = LoggerFactory.getLogger(SaveOrderInterface.class);
    private static Client restClient;
    private static WebResource webResource;

    @POST
    @Path("/order/saveOrder")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF-8")
    public Response processRequest(ClientRequest clientRequest) throws Exception {
        logger.info("-------------------------------------------------------------------------------------------------------");
        logger.info("Received Save Order request with : {}", clientRequest);
        String toSourceRequest = null;
        String sourceResponse = null;
        final float DEFAULT_FEE = 4.5f;

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
            } else if (SaveOrderCache.getInstance().getSaveOrderMap().containsKey(clientRequest.getBody().getVin())) {
                CarResponse response = JSONObject.parseObject(SaveOrderCache.getInstance().getByKey(clientRequest.getBody().getVin()).getResponseContent(), CarResponse.class);
                String replaceOrderNo = generateOrderNo();
                String orderNo = response.getData().getOrderNo();
                OrderMap orderMap = new OrderMap();
                orderMap.setReplaceOrderNo(replaceOrderNo);
                orderMap.setOrderNo(orderNo);
                OrderMapCache.getInstance().addOrderMap(orderMap);

                response.getData().setOrderNo(replaceOrderNo);
                response.getData().getCarModel().setTime(String.valueOf(System.currentTimeMillis()));
                logger.info("got from saveCache for vin: {}", clientRequest.getBody().getVin());

                //debit
                float balanceBeforeDebit = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getBalance();

                float debitFee = getDebitFee(clientRequest, DEFAULT_FEE, response);

                if (balanceBeforeDebit - debitFee < 0.00000001) {
                    CarResponse badResponse = new CarResponse();
                    badResponse.setCode("400015");
                    badResponse.setMsg("余额不足，请充值!!");
                    logger.info("return 400015, 余额不足，请充值!!");
                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
                }
                debit(clientRequest, response, balanceBeforeDebit, debitFee);

                if (Authentication.authenticateCallBackUrl(clientRequest.getBody().getCallBackUrl())) {
                    clientRequest.getBody().setOrderNo(replaceOrderNo);
                    callBack(clientRequest);

                }
                return Response.status(Response.Status.OK).entity(response).build();

            } else {
                //debit
                float balanceBeforeDebit = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getBalance();

                if (balanceBeforeDebit - DEFAULT_FEE < 0.00000001) {
                    CarResponse badResponse = new CarResponse();
                    badResponse.setCode("400015");
                    badResponse.setMsg("余额不足，请充值!!");
                    logger.info("return 400015, 余额不足，请充值!!");
                    return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
                }

                String url = PropertyUtil.readValue("source.url") + "/order/saveOrder_v1";
                webResource = restClient.resource(url);

                JSONObject json = new JSONObject();
                json.fluentPut("version", 1);
                json.fluentPut("timestamp", clientRequest.getTimestamp());
                json.fluentPut("ticket", clientRequest.getTicket());
                json.fluentPut("appKey", PropertyUtil.readValue("app.key"));
                json.fluentPut("appSecret", PropertyUtil.readValue("app.secret"));
                JSONObject body = new JSONObject();
                body.fluentPut("vin", clientRequest.getBody().getVin());
                if (clientRequest.getBody().getBrandId() != null) {
                    body.fluentPut("brandId", clientRequest.getBody().getBrandId());
                }
                if (clientRequest.getBody().getEnginNum() != null) {
                    body.fluentPut("enginNum", clientRequest.getBody().getEnginNum());
                }
                if (ConnectionPool.isProduction) {
                    body.fluentPut("callBackUrl", PropertyUtil.readValue("call.back.url.production"));
                } else {
                    body.fluentPut("callBackUrl", PropertyUtil.readValue("call.back.url"));
                }
                json.fluentPut("body", body);
                json.fluentPut("sign", EncryptUtil.getSignString(JSONObject.parseObject(json.toJSONString(), PackBody.class)));

                toSourceRequest = json.toJSONString();
                logger.info("request to url:{} with json:{}", url, toSourceRequest);
                ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
                CarResponse carResponse = response.getEntity(CarResponse.class);
                logger.info("get car response: {}", carResponse);
                sourceResponse = JSONObject.toJSONString(carResponse);

                if (carResponse.getCode().equals("0")) {
                    //cache it
                    logger.info("Cached carResponse: {}", JSONObject.toJSONString(carResponse));

                    Order saveOrder = new Order();
                    saveOrder.setVin(clientRequest.getBody().getVin());
                    saveOrder.setOrderNo(carResponse.getData().getOrderNo());
                    saveOrder.setResponseContent(JSONObject.toJSONString(carResponse));
                    SaveOrderCache.getInstance().addSaveOrder(saveOrder);

                    //debit
                    float debitFee = getDebitFee(clientRequest, DEFAULT_FEE, carResponse);

                    if (balanceBeforeDebit - debitFee < 0.00000001) {
                        CarResponse badResponse = new CarResponse();
                        badResponse.setCode("400015");
                        badResponse.setMsg("余额不足，请充值!!");
                        logger.info("return 400015, 余额不足，请充值!!");
                        return Response.status(Response.Status.OK).entity(JSONObject.toJSONString(badResponse)).build();
                    }
                    debit(clientRequest, carResponse, balanceBeforeDebit, debitFee);

                    if (Authentication.authenticateCallBackUrl(clientRequest.getBody().getCallBackUrl())) {
                        Thread.sleep(8000);
                        callBack(clientRequest);
                    }
                }

                logger.info("finish processing and return ok");
                return Response.status(Response.Status.OK).entity(carResponse).build();
            }
        } finally {
            //TODO:  create a new thread to handle inside DB stuff
            try {
                TransactionLog log = new TransactionLog();
                log.setAppKey(clientRequest.getAppKey());
                log.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                log.setTicket(clientRequest.getTicket());
                log.setRequestContent(clientRequest.toString());
                log.setLogType("original saveOrder request");
                TransLogAccessor.getInstance().addLog(log);

                if (toSourceRequest != null) {

                    TransactionLog requestlog = new TransactionLog();
                    requestlog.setAppKey(clientRequest.getAppKey());
                    requestlog.setRequestContent(toSourceRequest);
                    requestlog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    requestlog.setTicket(clientRequest.getTicket());
                    requestlog.setLogType("source saveOrder request");
                    TransLogAccessor.getInstance().addLog(requestlog);

                    TransactionLog responselog = new TransactionLog();
                    responselog.setAppKey(clientRequest.getAppKey());
                    responselog.setResponseContent(sourceResponse);
                    responselog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey())==null?"Invalid User":UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());
                    responselog.setTicket(clientRequest.getTicket());
                    responselog.setLogType("source saveOrder response");
                    TransLogAccessor.getInstance().addLog(responselog);
                }
            } catch (Exception e) {
                logger.error("Error while storing data. {}/ {}", e.getMessage(), e.getCause());
            }
        }
    }

    private float getDebitFee(ClientRequest clientRequest, float DEFAULT_FEE, CarResponse response) throws DBAccessException {
        float debitFee = 0f;
        Brand brand = BrandCache.getInstance().getByName(response.getData().getBrandName());
        if (brand == null) {
            brand = BrandCache.getInstance().getById(response.getData().getBrandId());
        }
        if (UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserFee() - 0 > 0.00000001) {
            debitFee = UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserFee();
        } else if (brand != null) {
            debitFee = brand.getPrice();
        }

        if (brand != null && brand.getIsSpecial().equals("Y")) {
            debitFee = brand.getPrice();
        }

        if (debitFee - 0 < 0.00000001) {
            debitFee = DEFAULT_FEE;
        }
        return debitFee;
    }

    private void debit(ClientRequest clientRequest, CarResponse response, float balanceBeforeDebit, float debitFee) throws DBAccessException {
        DebitLog debitLog = new DebitLog();
        debitLog.setAppKey(clientRequest.getAppKey());
        debitLog.setBalanceBeforeDebit(balanceBeforeDebit);
        debitLog.setBrandId(response.getData().getBrandId());
        debitLog.setBrandName(response.getData().getBrandName());
        debitLog.setDebitFee(debitFee);
        debitLog.setOrderNo(response.getData().getOrderNo());
        debitLog.setVin(clientRequest.getBody().getVin());
        debitLog.setUserName(UserCache.getInstance().getByKey(clientRequest.getAppKey()).getUserName());

        DebitLogAccessor.getInstance().addLog(debitLog);
        User updatedUser = UserCache.getInstance().getByKey(clientRequest.getAppKey());
        updatedUser.setBalance(balanceBeforeDebit - debitFee);
        UserCache.getInstance().updateUser(updatedUser);
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
