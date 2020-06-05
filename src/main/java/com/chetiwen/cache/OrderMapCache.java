package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.OrderMapAccessor;
import com.chetiwen.db.model.OrderMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMapCache {
    private Logger logger = LoggerFactory.getLogger(OrderMapCache.class);

    private static OrderMapCache instance;

    private Map<String, OrderMap> idMap;

    private OrderMapCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized OrderMapCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new OrderMapCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<OrderMap> list = OrderMapAccessor.getInstance().getAllOrderMappings();

        list.forEach(orderMap->{
            idMap.put(orderMap.getReplaceOrderNo(), orderMap);
        });

        logger.info("OrderMapCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public OrderMap getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, OrderMap> getOrderMap() {
        return idMap;
    }

    public void refreshCache() throws DBAccessException {
        if (idMap.size() == 0) {
            reload();
        }
    }


    public void addOrderMap(OrderMap orderMap) throws DBAccessException {
        logger.info("add into OrderMap cache: {}", orderMap);

        OrderMapAccessor.getInstance().addOrderMap(orderMap);
        idMap.put(orderMap.getReplaceOrderNo(), orderMap);
    }



}
