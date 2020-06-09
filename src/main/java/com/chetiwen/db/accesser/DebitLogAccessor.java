package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.DebitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebitLogAccessor {

    private Logger logger = LoggerFactory.getLogger(DebitLogAccessor.class);

    private static DebitLogAccessor instance;

    private DebitLogAccessor() {
    }

    public static synchronized DebitLogAccessor getInstance() {
        if (instance == null) {
            instance = new DebitLogAccessor();
        }
        return instance;
    }

    public void addLog(DebitLog log) throws DBAccessException {
        logger.info("Received add debit log data request. log: {}", log.toString());

        String sql = "insert into debit_log(order_no, vin, partner_id, brand_id, brand_name, balance_before_debit, debit_fee) " +
                "values (?,?,?,?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, log.getOrderNo());
            preparedStatement.setString(2, log.getVin());
            preparedStatement.setString(3, log.getPartnerId());
            preparedStatement.setString(4, log.getBrandId());
            preparedStatement.setString(5, log.getBrandName());
            preparedStatement.setFloat(6, log.getBalanceBeforeDebit());
            preparedStatement.setFloat(7, log.getDebitFee());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted debit_log record");
    }

    public List<DebitLog> getDebitLogs() throws DBAccessException{
        logger.info("Get debit logs request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from debit_log ");
            List<DebitLog> list = new ArrayList<>();
            while(rs.next()){
                DebitLog userOrder = new DebitLog();
                userOrder.setLogId(rs.getInt("log_id"));
                userOrder.setPartnerId(rs.getString("partner_id"));
                userOrder.setBrandName(rs.getString("brand_name"));
                userOrder.setBrandId(rs.getString("brand_id"));
                userOrder.setCreateTime(rs.getTimestamp("create_time"));
                userOrder.setDebitFee(rs.getFloat("debit_fee"));
                userOrder.setOrderNo(rs.getString("order_no"));
                userOrder.setVin(rs.getString("vin"));
                list.add(userOrder);
            }
            connection.close();
//            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

}
