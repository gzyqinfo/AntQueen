package com.chetiwen.controll;

import com.chetiwen.cache.GetOrderCache;
import com.chetiwen.cache.OrderCallbackCache;
import com.chetiwen.db.model.Order;
import com.chetiwen.db.model.OrderCallback;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class CallbackProcessor {
    private Logger logger = LoggerFactory.getLogger(CallbackProcessor.class);


    public void callback(String url, String orderNo) {
        logger.info("callback task to {} ");

        Runnable callbackAction = () -> {
            try {
                if (GetOrderCache.getInstance().getGetOrderMap().containsKey(orderNo)) {
                    Order order = GetOrderCache.getInstance().getByKey(orderNo);
                    ClientConfig config = new DefaultClientConfig();
                    config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
                    Client restClient = Client.create(config);
                    logger.info("call back to {}", url);
                    WebResource webResource = restClient.resource(url);
                    ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, order.getResponseContent());
                    logger.info("receive callback return statement {}", response.getEntity(Object.class).toString());
                    OrderCallbackCache.getInstance().delOrderCallback(orderNo);
                } else {
                    logger.info("There is no order currently, store it to orderCallback");
                    OrderCallback orderCallback = new OrderCallback();
                    orderCallback.setUrl(url);
                    orderCallback.setOrderNo(orderNo);
                    OrderCallbackCache.getInstance().addOrderCallback(orderCallback);
                }
            } catch (Exception e) {
                logger.error("Fail to do callback");
                e.printStackTrace();
            }

        };
        new Thread(callbackAction).start();

    }
}
