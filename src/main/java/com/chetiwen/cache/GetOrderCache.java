package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.GetOrderAccessor;
import com.chetiwen.db.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOrderCache {
    private Logger logger = LoggerFactory.getLogger(GetOrderCache.class);

    private static GetOrderCache instance;

    private Map<String, Order> idMap;

    private GetOrderCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized GetOrderCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new GetOrderCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<Order> list = GetOrderAccessor.getInstance().getAllGetOrders();

        list.forEach(order->{
            idMap.put(order.getOrderNo(), order);
        });

        logger.info("GetOrderCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public Order getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, Order> getGetOrderMap() {
        return idMap;
    }

    public void addGetOrder(Order getOrder) throws DBAccessException {
        logger.info("add into GetOrder cache: {}", getOrder);

        GetOrderAccessor.getInstance().addGetOrder(getOrder);
        idMap.put(getOrder.getOrderNo(), getOrder);
    }

}
