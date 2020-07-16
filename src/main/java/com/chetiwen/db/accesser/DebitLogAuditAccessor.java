package com.chetiwen.db.accesser;


import com.chetiwen.db.ConnectionPool;
import com.chetiwen.db.DBAccessException;
import com.chetiwen.db.SqlHelper;
import com.chetiwen.db.model.DebitLogAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DebitLogAuditAccessor {

    private Logger logger = LoggerFactory.getLogger(DebitLogAuditAccessor.class);

    private static DebitLogAuditAccessor instance;

    private DebitLogAuditAccessor() {
    }

    public static synchronized DebitLogAuditAccessor getInstance() {
        if (instance == null) {
            instance = new DebitLogAuditAccessor();
        }
        return instance;
    }


    public List<DebitLogAudit> getDebitLogAudit(String partnerId) throws DBAccessException{
        logger.info("Get debitLog audit request");
        Connection connection = ConnectionPool.getConnection();
        try {
            ResultSet rs = SqlHelper.executeQuery (connection, "select * from debit_log_audit where partner_id = '"+partnerId+"' or partner_id = '"+partnerId+"->"+partnerId+"' order by create_time desc");
            List<DebitLogAudit> list = new ArrayList<>();
            while(rs.next()){
                DebitLogAudit debitLogAudit = new DebitLogAudit();
                debitLogAudit.setOperator(rs.getString("who_did_it"));
                debitLogAudit.setAction(rs.getString("action"));
                debitLogAudit.setCreateTime(rs.getTimestamp("create_time"));
                debitLogAudit.setBrandId(rs.getString("brand_id"));
                debitLogAudit.setBrandName(rs.getString("brand_name"));
                debitLogAudit.setBalanceBeforeDebit(rs.getString("balance_before_debit"));
                debitLogAudit.setDebitFee(rs.getString("debit_fee"));
                debitLogAudit.setPartnerId(rs.getString("partner_id"));
                debitLogAudit.setFeeType(rs.getString("fee_type"));
                debitLogAudit.setOrderNo(rs.getString("order_no"));
                debitLogAudit.setVin(rs.getString("vin"));

                //for China TimeZone
                debitLogAudit.setCreateTime(new Timestamp(debitLogAudit.getCreateTime().getTime()+1000*3600*8l));

                list.add(debitLogAudit);
            }
            connection.close();
            logger.info("returned {} row(s) data", list.size());
            return list;
        } catch (SQLException e) {
            throw new DBAccessException(connection, e);
        }
    }

}
