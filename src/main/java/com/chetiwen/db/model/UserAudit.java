package com.chetiwen.db.model;

import java.sql.Timestamp;

public class UserAudit {

    private String operator;
    private Timestamp createTime;
    private String action;
    private String userName;
    private String partnerId;
    private String partnerKey;
    private String balance;
    private String isValid;
    private String dataSource;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String toString() {
        return "UserAudit{" +
                "operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", action='" + action + '\'' +
                ", userName='" + userName + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", partnerKey='" + partnerKey + '\'' +
                ", balance='" + balance + '\'' +
                ", isValid='" + isValid + '\'' +
                ", dataSource='" + dataSource + '\'' +
                '}';
    }
}
