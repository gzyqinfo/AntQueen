package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetOrderAccessor {

    private Logger logger = LoggerFactory.getLogger(GetOrderAccessor.class);

    private static GetOrderAccessor instance;

    private GetOrderAccessor() {
    }

    public static synchronized GetOrderAccessor getInstance() {
        if (instance == null) {
            instance = new GetOrderAccessor();
        }
        return instance;
    }

    public List<Order> getAllGetOrders() throws DBAccessException{
        logger.info("Get all get orders request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select order_no, vin, create_time from get_order");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order getOrder = new Order();
                getOrder.setCreateTime(rs.getTimestamp("create_time"));
                getOrder.setOrderNo(rs.getString("order_no"));
//                getOrder.setResponseContent(rs.getString("response_content"));
                getOrder.setVin(rs.getString("vin"));
                list.add(getOrder);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }


    public Order getOrderByOrderNo(String orderNo) throws DBAccessException{
        logger.info("Get order by order no: {} ", orderNo);
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from get_order where order_no = \"" + orderNo + "\"");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order getOrder = new Order();
                getOrder.setCreateTime(rs.getTimestamp("create_time"));
                getOrder.setOrderNo(rs.getString("order_no"));
                getOrder.setResponseContent(rs.getString("response_content"));
                getOrder.setVin(rs.getString("vin"));
                list.add(getOrder);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            if (list.size() == 1) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addGetOrder(Order getOrder) throws DBAccessException {
        logger.info("Received add get order request. Order : {}", getOrder.toString());

        String sql = "insert into get_order(vin, order_no, response_content) " +
                "values (?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, getOrder.getVin());
            preparedStatement.setString(2, getOrder.getOrderNo());
            preparedStatement.setString(3, getOrder.getResponseContent());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted GetOrder record");
    }

}
