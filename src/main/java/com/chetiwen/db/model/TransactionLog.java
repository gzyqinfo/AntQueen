package com.chetiwen.db.model;

import java.sql.Timestamp;

public class TransactionLog {
    private int logId;
    private String logType;
    private String userName;
    private String partnerId;
    private Timestamp createTime;
    private String transactionContent;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getTransactionContent() {
        return transactionContent;
    }

    public void setTransactionContent(String transactionContent) {
        this.transactionContent = transactionContent;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    @Override
    public String toString() {
        return "TransactionLog{" +
                "logId=" + logId +
                ", logType='" + logType + '\'' +
                ", userName='" + userName + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", createTime=" + createTime +
                ", transactionContent='" + transactionContent + '\'' +
                '}';
    }
}
