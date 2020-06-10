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

    private Map<String, Order> vinMap;

    private SaveOrderCache() throws DBAccessException {
        vinMap = new HashMap<>();
        reload();
    }

    public static synchronized SaveOrderCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new SaveOrderCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        vinMap.clear();

        List<Order> list = SaveOrderAccessor.getInstance().getAllSavedOrders();

        list.forEach(order->{
            vinMap.put(order.getVin(), order);
        });

        logger.info("SaveOrderCache reloaded. {} record(s) are refreshed.", vinMap.size());
    }

    public Order getByKey(String key) {
        return vinMap.get(key);
    }

    public Map<String, Order> getSaveOrderMap() {
        return vinMap;
    }

    public void refreshCache() throws DBAccessException {
        if (vinMap.size() == 0) {
            reload();
        }
    }


    public void addSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("add into SaveOrder cache: {}", saveOrder);

        SaveOrderAccessor.getInstance().addSaveOrder(saveOrder);
        vinMap.put(saveOrder.getVin(), saveOrder);
    }

    public void updateSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("update saveOrder cache for user: {}/{}", saveOrder.getVin(), saveOrder);

        SaveOrderAccessor.getInstance().updateSaveOrder(saveOrder);
        vinMap.put(saveOrder.getVin(), saveOrder);
    }

    public void delSaveOrder(String orderNo)  throws DBAccessException {
        logger.info("remove saveOrder cache for orderNo: {}", orderNo);

        SaveOrderAccessor.getInstance().delSaveOrder(orderNo);
        vinMap.values().removeIf(value -> value.getOrderNo().equals(orderNo));

    }
}
