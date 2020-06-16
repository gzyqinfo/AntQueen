package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.OrderMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapAccessor {

    private Logger logger = LoggerFactory.getLogger(OrderMapAccessor.class);

    private static OrderMapAccessor instance;

    private OrderMapAccessor() {
    }

    public static synchronized OrderMapAccessor getInstance() {
        if (instance == null) {
            instance = new OrderMapAccessor();
        }
        return instance;
    }

    public List<OrderMap> getAllOrderMappings() throws DBAccessException{
        logger.info("Get all order mappings request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from order_map");
            List<OrderMap> list = new ArrayList<>();
            while(rs.next()){
                OrderMap orderMap = new OrderMap();
                orderMap.setReplaceOrderNo(rs.getString("replace_order_no"));
                orderMap.setOrderNo(rs.getString("order_no"));
                list.add(orderMap);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addOrderMap(OrderMap orderMap) throws DBAccessException {
        logger.info("Received add order map request. Order : {}", orderMap.toString());

        String sql = "insert into order_map(replace_order_no, order_no) " +
                "values (?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderMap.getReplaceOrderNo());
            preparedStatement.setString(2, orderMap.getOrderNo());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted OrderMap record");
    }

}
