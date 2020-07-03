package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.UserAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAuditAccessor {

    private Logger logger = LoggerFactory.getLogger(UserAuditAccessor.class);

    private static UserAuditAccessor instance;

    private UserAuditAccessor() {
    }

    public static synchronized UserAuditAccessor getInstance() {
        if (instance == null) {
            instance = new UserAuditAccessor();
        }
        return instance;
    }


    public List<UserAudit> getUserAudit(String partnerId) throws DBAccessException{
        logger.info("Get user audit request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from user_audit where partner_id like '" + partnerId + "%' order by create_time desc");
            List<UserAudit> list = new ArrayList<>();
            while(rs.next()){
                UserAudit userAudit = new UserAudit();
                userAudit.setOperator(rs.getString("who_did_it"));
                userAudit.setAction(rs.getString("action"));
                userAudit.setCreateTime(rs.getTimestamp("create_time"));
                userAudit.setBalance(rs.getString("balance"));
                userAudit.setDataSource(rs.getString("data_source"));
                userAudit.setIsValid(rs.getString("is_valid"));
                userAudit.setPartnerId(rs.getString("partner_id"));
                userAudit.setPartnerKey(rs.getString("partner_key"));
                userAudit.setUserName(rs.getString("user_name"));

                //for China TimeZone
                userAudit.setCreateTime(new Timestamp(userAudit.getCreateTime().getTime()+1000*3600*8l));

                list.add(userAudit);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

}
