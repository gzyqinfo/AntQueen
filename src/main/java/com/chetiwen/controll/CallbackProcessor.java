package com.chetiwen.controll;

import com.alibaba.fastjson.JSONObject;
import com.chetiwen.cache.GetOrderCache;
import com.chetiwen.cache.OrderCallbackCache;
import com.chetiwen.cache.OrderMapCache;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.OrderCallback;
import com.chetiwen.object.antqueen.AntOrderResponse;
import com.chetiwen.object.antqueen.OrderReportResponse;
import com.chetiwen.object.qucent.QucentOrderResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Timer;
import java.util.TimerTask;

public class CallbackProcessor {
    private Logger logger = LoggerFactory.getLogger(CallbackProcessor.class);


    public void callback(String url, String replaceOrderNo) {
        logger.info("callback task to {} for order: {}", url, replaceOrderNo);

        Runnable callbackAction = () -> {
            try {
                String orderNo = OrderMapCache.getInstance().getByKey(replaceOrderNo).getOrderNo();
                if (GetOrderCache.getInstance().getGetOrderMap().containsKey(orderNo)) {
                    Order order = GetOrderCache.getInstance().getByKey(orderNo);

                    String callbackContent = order.getResponseContent();

                    if (orderNo.length()>20) { // Order from Qucent
                        callbackContent = DataConvertor.convertToAntQueenOrder(JSONObject.parseObject(order.getResponseContent(), QucentOrderResponse.class)).toString();
                    }

                    ClientConfig config = new DefaultClientConfig();
                    config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                    Client restClient = Client.create(config);
                    logger.info("call back to {}", url);
                    WebResource webResource = restClient.resource(url);
                    ClientResponse response = getClientResponse(url, callbackContent, webResource);
                    if (response != null && Response.Status.OK.getStatusCode() == response.getStatus()) {
                        OrderCallbackCache.getInstance().delOrderCallback(replaceOrderNo);
                        logger.info("receive callback return statement {}", response.getEntity(Object.class).toString());
                    } else {
                        logger.info("call back to {} unsuccessfully, to retry 5 more times", url);
                        int number = 5;//设置运行五次
                        Timer timer = new Timer();
                        String finalCallbackContent = callbackContent;

                        TimerTask task = new TimerTask() {
                            int count = 1;	//从1开始计数，每运行一次timertask次数加一，运行制定次数后结束。
                            @Override
                            public void run() {
                                if(count<number){
                                    logger.info("call back to {} unsuccessfully, to retry {} more times", url, number-count);
                                    ClientResponse clientResponse = getClientResponse(url, finalCallbackContent, webResource);
                                    if (clientResponse != null && Response.Status.OK.getStatusCode() == clientResponse.getStatus()) {
                                        try {
                                            logger.info("call back to {} successfully", url);
                                            OrderCallbackCache.getInstance().delOrderCallback(replaceOrderNo);
                                            timer.cancel();
                                        } catch (DBAccessException e) {
                                            logger.error("Fail to call back : ", e);
                                            e.printStackTrace();
                                        }
                                    }
                                } else  {
                                    logger.info("call back to {} unsuccessfully eventually", url);
                                    timer.cancel();
                                }
                                count++;
                            }
                        };
                        timer.schedule(task, 0,1000*60*2);//每隔2分钟运行一次
                    }

                } else {
                    logger.info("There is no order currently, store it to orderCallback");
                    OrderCallback orderCallback = new OrderCallback();
                    orderCallback.setUrl(url);
                    orderCallback.setOrderNo(replaceOrderNo);
                    OrderCallbackCache.getInstance().addOrderCallback(orderCallback);

                }
            } catch (Exception e) {
                logger.error("Fail to do callback, {}", e);
                e.printStackTrace();
            }

        };
        new Thread(callbackAction).start();

    }

    private ClientResponse getClientResponse(String url, String callbackContent, WebResource webResource) {
        ClientResponse response = null;
        try {
            response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, callbackContent);
        } catch (Exception e) {
            logger.error("fail to connect to {}, with error: {}", url, e);
        }
        return response;
    }
}
