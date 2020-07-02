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
                saveOrder.setDataSource(rs.getString("data_source"));
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

        String sql = "insert into save_order(vin, order_no, response_content, data_source) " +
                "values (?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, saveOrder.getVin());
            preparedStatement.setString(2, saveOrder.getOrderNo());
            preparedStatement.setString(3, saveOrder.getResponseContent());
            preparedStatement.setString(4, saveOrder.getDataSource());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted SaveOrder record");
    }


    public void delSaveOrder(String orderNo) throws DBAccessException {
        logger.info("Received del saveOrder data request. orderNo: {}", orderNo);

        String sql = "delete from save_order where order_no = \""+orderNo+"\"";

        Connection connection = ConnectionPool.getConnection();
        try {
            SqlHelper.executeUpdate (connection, sql);
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("deleted save_order record");
    }

    public void housekeepSaveOrder(int days) throws DBAccessException {
        logger.info("Received save_order housekeeping request. keep days: {}", days);

        String sql = "delete from save_order where DATEDIFF(CURTIME(), create_time) > ?";

        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, days);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            logger.error("error while accessing DB! {}" , e.getMessage());
        }
        logger.info("save_order housekept.");
    }
}
