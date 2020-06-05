package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.TransactionLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransLogAccessor {

    private Logger logger = LoggerFactory.getLogger(TransLogAccessor.class);

    private static TransLogAccessor instance;

    private TransLogAccessor() {
    }

    public static synchronized TransLogAccessor getInstance() {
        if (instance == null) {
            instance = new TransLogAccessor();
        }
        return instance;
    }

    public void addLog(TransactionLog log) throws DBAccessException {
        logger.info("Received add transaction log data request. log: {}", log.toString());

        String sql = "insert into transaction_log(user_name, app_key,ticket, request_content, response_content, log_type) " +
                "values (?,?,?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, log.getUserName());
            preparedStatement.setString(2, log.getAppKey());
            preparedStatement.setString(3, log.getTicket());
            preparedStatement.setString(4, log.getRequestContent());
            preparedStatement.setString(5, log.getResponseContent());
            preparedStatement.setString(6, log.getLogType());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted transaction_log record");
    }

}
