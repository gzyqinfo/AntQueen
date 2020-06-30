package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccessor {

    private Logger logger = LoggerFactory.getLogger(UserAccessor.class);

    private static UserAccessor instance;

    private UserAccessor() {
    }

    public static synchronized UserAccessor getInstance() {
        if (instance == null) {
            instance = new UserAccessor();
        }
        return instance;
    }

    public List<User> getAllUsers() throws DBAccessException{
        logger.info("Get all Users request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from user");
            List<User> list = new ArrayList<>();
            while(rs.next()){
                User user = new User();
                user.setPartnerId(rs.getString("partner_id"));
                user.setBalance(rs.getFloat("balance"));
                user.setPartnerKey(rs.getString("partner_key"));
                user.setUserName(rs.getString("user_name"));
                user.setIsValid(rs.getInt("is_valid"));
                user.setDataSource(rs.getString("data_source"));

                list.add(user);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

    public void addUser(User user) throws DBAccessException {
        logger.info("Received add user data request. message type: {}", user.toString());

        String sql = "insert into user(user_name, partner_id, partner_key, balance, data_source) " +
                "values (?,?,?,?,?)";
        Connection connection = ConnectionPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPartnerId());
            preparedStatement.setString(3, user.getPartnerKey());
            preparedStatement.setFloat(4, user.getBalance());
            preparedStatement.setString(5, user.getDataSource());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("inserted User record");
    }

    public void updateUser(User user) throws DBAccessException {
        logger.info("Received update user data request. updated user: {}", user);

        String sql = new StringBuilder().append("update user ")
                .append("set user_name = \"").append(user.getUserName()).append("\"")
                .append(", balance = ").append(user.getBalance())
                .append(", data_source = \"").append(user.getDataSource()).append("\"")
                .append(" where partner_id = \"").append(user.getPartnerId()).append("\"")
                .toString();
        Connection connection = ConnectionPool.getConnection();
        try {
            SqlHelper.executeUpdate (connection, sql);
            connection.close();
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
        logger.info("updated user record for partner_id : {}", user.getPartnerId());
    }
}
