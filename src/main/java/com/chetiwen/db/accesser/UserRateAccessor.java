package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.UserRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRateAccessor {

    private Logger logger = LoggerFactory.getLogger(UserRateAccessor.class);

    private static UserRateAccessor instance;

    private UserRateAccessor() {
    }

    public static synchronized UserRateAccessor getInstance() {
        if (instance == null) {
            instance = new UserRateAccessor();
        }
        return instance;
    }

    public List<UserRate> getAllUserRates() throws DBAccessException{
        logger.info("Get all User rate request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from user_rate");
            List<UserRate> list = new ArrayList<>();
            while(rs.next()){
                UserRate userRate = new UserRate();
                userRate.setPartnerId(rs.getString("partner_id"));
                userRate.setBrandId(rs.getInt("brand_id"));
                userRate.setBrandName(rs.getString("brand_name"));
                userRate.setPrice(rs.getFloat("price"));

                list.add(userRate);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addUserRate(UserRate userRate) throws DBAccessException {
        logger.info("Received add user rate data request. message type: {}", userRate.toString());

        String sql = "insert into user_rate(partner_id, brand_id, brand_name, price) " +
                "values (?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userRate.getPartnerId());
            preparedStatement.setInt(2, userRate.getBrandId());
            preparedStatement.setString(3, userRate.getBrandName());
            preparedStatement.setFloat(4, userRate.getPrice());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted User Rate record");
    }

    public void updateUserRate(UserRate userRate) throws DBAccessException {
        logger.info("Received update user rate data request. updated user: {}", userRate);

        String sql = new StringBuilder().append("update user_rate ")
                .append("set price = ").append(userRate.getPrice())
                .append(" where partner_id = \"").append(userRate.getPartnerId()).append("\"")
                .append(" and brand_id =").append(userRate.getBrandId())
                .toString();
        Connection connection = ConnectionPool.getConnection();
        try {
            SqlHelper.executeUpdate (connection, sql);
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("updated user rate record for partner_id : {}", userRate.getPartnerId());
    }
}
