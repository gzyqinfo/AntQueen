package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.OrderCallbackAccessor;
import com.chetiwen.db.model.OrderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderCallbackCache {
    private Logger logger = LoggerFactory.getLogger(OrderCallbackCache.class);

    private static OrderCallbackCache instance;

    private Map<String, OrderCallback> idMap;

    private OrderCallbackCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized OrderCallbackCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new OrderCallbackCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<OrderCallback> list = OrderCallbackAccessor.getInstance().getAllOrderCallbacks();

        list.forEach(orderCallback->{
            idMap.put(orderCallback.getOrderNo(), orderCallback);
        });

        logger.info("OrderCallbackCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public OrderCallback getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, OrderCallback> getOrderCallback() {
        return idMap;
    }

    public void delOrderCallback(String orderNo)  throws DBAccessException {
        logger.info("remove OrderCallback cache for orderNo: {}", orderNo);

        OrderCallbackAccessor.getInstance().delOrderCallback(orderNo);
        idMap.values().removeIf(value -> value.getOrderNo().equals(orderNo));
    }

}
