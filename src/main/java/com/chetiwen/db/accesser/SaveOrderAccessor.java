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

public class SaveOrderAccessor {

    private Logger logger = LoggerFactory.getLogger(SaveOrderAccessor.class);

    private static SaveOrderAccessor instance;

    private SaveOrderAccessor() {
    }

    public static synchronized SaveOrderAccessor getInstance() {
        if (instance == null) {
            instance = new SaveOrderAccessor();
        }
        return instance;
    }

    public List<Order> getAllSavedOrders() throws DBAccessException{
        logger.info("Get all saved orders request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from save_order");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order saveOrder = new Order();
                saveOrder.setCreateTime(rs.getTimestamp("create_time"));
                saveOrder.setOrderNo(rs.getString("order_no"));
                saveOrder.setResponseContent(rs.getString("response_content"));
                saveOrder.setVin(rs.getString("vin"));
                list.add(saveOrder);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("Received add save order request. Order : {}", saveOrder.toString());

        String sql = "insert into save_order(vin, order_no, response_content) " +
                "values (?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, saveOrder.getVin());
            preparedStatement.setString(2, saveOrder.getOrderNo());
            preparedStatement.setString(3, saveOrder.getResponseContent());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted SaveOrder record");
    }

    public void updateSaveOrder(Order saveOrder) throws DBAccessException {
        logger.info("Received save order data request. updated order: {}", saveOrder);

        String sql = new StringBuilder().append("update save_order ")
                .append("set order_no = \"").append(saveOrder.getOrderNo()).append("\"")
                .append(", response_content = \"").append(saveOrder.getOrderNo()).append("\"")
                .append(" where vin = \"").append(saveOrder.getVin()).append("\"")
                .toString();
        Connection connection = ConnectionPool.getConnection();
        try {
            SqlHelper.executeUpdate (connection, sql);
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("updated save_order record for vin : {}", saveOrder.getVin());
    }
}
