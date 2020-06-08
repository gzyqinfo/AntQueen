package com.chetiwen.db.accesser;


import com.chetiwen.cache.UserCache;
import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.model.TransactionLog;
import com.chetiwen.object.AntRequest;
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

        String sql = "insert into transaction_log(log_type, user_name, partner_id, transaction_content) " +
                "values (?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, log.getLogType());
            preparedStatement.setString(2, log.getUserName());
            preparedStatement.setString(3, log.getPartnerId());
            preparedStatement.setString(4, log.getTransactionContent());

            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted transaction_log record");
    }


    public void AddTransLog(AntRequest antRequest, String content, String logType) throws DBAccessException {
        TransactionLog log = new TransactionLog();
        log.setPartnerId(antRequest.getPartnerId());
        log.setUserName(UserCache.getInstance().getByKey(antRequest.getPartnerId()).getUserName());
        log.setTransactionContent(content);
        log.setLogType(logType);
        addLog(log);
    }
}
