package com.chetiwen.db;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelper {
    public static Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    /**
     * execute 、insert、delete statements，return rows of infected
     *
     * @param connection
     * @param sql
     * @return
     */
    public static int executeUpdate(Connection connection, String sql) throws SQLException {
        int resCount = 0;
        if (StringUtils.isBlank(sql)) {
            logger.warn("sql can't be null");
            return resCount;
        }
        logger.info("sql--> {}",sql);
        PreparedStatement ps = connection.prepareStatement(sql);
        resCount = ps.executeUpdate();
        logger.info("{} row(s) data updated", resCount);
        return resCount;
    }

    /**
     * Execute query
     *
     * @param connection
     * @param sql
     * @return
     */
    public static ResultSet executeQuery(Connection connection, String sql) throws SQLException {
        if (StringUtils.isBlank(sql)) {
            logger.warn("sql can't be null");
            return null;
        }
        logger.info("sql--> {}",sql);

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        return rs;
    }


}
