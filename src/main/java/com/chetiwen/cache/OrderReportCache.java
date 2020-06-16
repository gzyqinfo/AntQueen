package com.chetiwen.cache;

import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.accesser.OrderReportAccessor;
import com.chetiwen.db.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderReportCache {
    private Logger logger = LoggerFactory.getLogger(OrderReportCache.class);

    private static OrderReportCache instance;

    private Map<String, Order> idMap;

    private OrderReportCache() throws DBAccessException {
        idMap = new HashMap<>();
        reload();
    }

    public static synchronized OrderReportCache getInstance() throws DBAccessException {
        if (instance == null) {
            instance = new OrderReportCache();
        }
        return instance;
    }

    public void reload() throws DBAccessException {
        idMap.clear();

        List<Order> list = OrderReportAccessor.getInstance().getAllOrderReports();

        list.forEach(order->{
            idMap.put(order.getOrderNo(), order);
        });

        logger.info("OrderReportCache reloaded. {} record(s) are refreshed.", idMap.size());
    }

    public Order getByKey(String key) {
        return idMap.get(key);
    }

    public Map<String, Order> getOrderReportMap() {
        return idMap;
    }

    public void addOrderReport(Order orderReport) throws DBAccessException {
        logger.info("add into orderReport cache: {}", orderReport);

        OrderReportAccessor.getInstance().addOrderReport(orderReport);
        idMap.put(orderReport.getOrderNo(), orderReport);
    }

}
