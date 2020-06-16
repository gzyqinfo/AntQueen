package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.OrderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderCallbackAccessor {

    private Logger logger = LoggerFactory.getLogger(OrderCallbackAccessor.class);

    private static OrderCallbackAccessor instance;

    private OrderCallbackAccessor() {
    }

    public static synchronized OrderCallbackAccessor getInstance() {
        if (instance == null) {
            instance = new OrderCallbackAccessor();
        }
        return instance;
    }

    public List<OrderCallback> getAllOrderCallbacks() throws DBAccessException{
        logger.info("Get all order callbacks request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from order_callback");
            List<OrderCallback> list = new ArrayList<>();
            while(rs.next()){
                OrderCallback orderCallback = new OrderCallback();
                orderCallback.setUrl(rs.getString("url"));
                orderCallback.setOrderNo(rs.getString("order_no"));
                list.add(orderCallback);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addOrderCallback(OrderCallback orderCallback) throws DBAccessException {
        logger.info("Received add order callback request. Order : {}", orderCallback.toString());

        String sql = "insert into order_callback(url, order_no) " +
                "values (?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderCallback.getUrl());
            preparedStatement.setString(2, orderCallback.getOrderNo());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted OrderCallback record");
    }

    public void delOrderCallback(String orderNo) throws DBAccessException {
        logger.info("Received del OrderCallback data request. orderNo: {}", orderNo);

        String sql = "delete from order_callback where order_no = \""+orderNo+"\"";

        Connection connection = ConnectionPool.getConnection();
        try {
            SqlHelper.executeUpdate (connection, sql);
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("deleted order_callback record");
    }
}
