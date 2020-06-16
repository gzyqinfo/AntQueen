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

public class OrderReportAccessor {

    private Logger logger = LoggerFactory.getLogger(OrderReportAccessor.class);

    private static OrderReportAccessor instance;

    private OrderReportAccessor() {
    }

    public static synchronized OrderReportAccessor getInstance() {
        if (instance == null) {
            instance = new OrderReportAccessor();
        }
        return instance;
    }

    public List<Order> getAllOrderReports() throws DBAccessException{
        logger.info("Get all order reports request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from order_report");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order orderReport = new Order();
                orderReport.setCreateTime(rs.getTimestamp("create_time"));
                orderReport.setOrderNo(rs.getString("order_no"));
                orderReport.setResponseContent(rs.getString("response_content"));
                orderReport.setVin(rs.getString("vin"));
                list.add(orderReport);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public Order getReportByVin(String vin) throws DBAccessException{
        logger.info("Get order report by vin  request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from order_report where vin = \"" + vin + "\"");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order orderReport = new Order();
                orderReport.setCreateTime(rs.getTimestamp("create_time"));
                orderReport.setOrderNo(rs.getString("order_no"));
                orderReport.setResponseContent(rs.getString("response_content"));
                orderReport.setVin(rs.getString("vin"));
                list.add(orderReport);
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

    public Order getReportByOrderNo(String orderNo) throws DBAccessException{
        logger.info("Get all order by order  request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from order_report where order_no = \"" + orderNo + "\"");
            List<Order> list = new ArrayList<>();
            while(rs.next()){
                Order orderReport = new Order();
                orderReport.setCreateTime(rs.getTimestamp("create_time"));
                orderReport.setOrderNo(rs.getString("order_no"));
                orderReport.setResponseContent(rs.getString("response_content"));
                orderReport.setVin(rs.getString("vin"));
                list.add(orderReport);
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

    public void addOrderReport(Order orderReport) throws DBAccessException {
        logger.info("Received add get order request. Order : {}", orderReport.toString());

        String sql = "insert into order_report(vin, order_no, response_content) " +
                "values (?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, orderReport.getVin());
            preparedStatement.setString(2, orderReport.getOrderNo());
            preparedStatement.setString(3, orderReport.getResponseContent());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted OrderReport record");
    }

}
