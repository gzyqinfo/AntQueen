package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.SaveOrderAccessor;
import com.chetiwen.db.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveOrderCache {
    private Logger logger = LoggerFactory.getLogger(SaveOrderCache.class);

    private static SaveOrderCache instance;

    private Map<String, Order> idMap;

    private SaveOrderCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized SaveOrderCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new SaveOrderCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<Order> list = SaveOrderAccessor.getInstance().getAllSavedOrders();

        list.forEach(order->{
            idMap.put(order.getVin(), order);
        });

        logger.info("SaveOrderCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public Order getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, Order> getSaveOrderMap() {
        return idMap;
    }

    public void refreshCache() throws DBAccessException {
        if (idMap.size() == 0) {
            reload();
        }
    }


    public void addSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("add into SaveOrder cache: {}", saveOrder);

        SaveOrderAccessor.getInstance().addSaveOrder(saveOrder);
        idMap.put(saveOrder.getVin(), saveOrder);
    }

    public void updateSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("update saveOrder cache for user: {}/{}", saveOrder.getVin(), saveOrder);

        SaveOrderAccessor.getInstance().updateSaveOrder(saveOrder);
        idMap.put(saveOrder.getVin(), saveOrder);
    }

}
