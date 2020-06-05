package com.chetiwen.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DBAccessException extends Exception {
    private Logger logger = LoggerFactory.getLogger(DBAccessException.class);
    public DBAccessException(String message) {
        super(message);
    }

    public DBAccessException(Connection connection, SQLException e) {
        super(e.getMessage());
        try {
            connection.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
        logger.error("error while accessing DB! {}" , e.getMessage());
    }
}
