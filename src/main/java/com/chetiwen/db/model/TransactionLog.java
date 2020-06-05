package com.chetiwen.db.model;

import java.sql.Timestamp;

public class TransactionLog {
    private int logId;
    private String logType;
    private String userName;
    private String appKey;
    private Timestamp createTime;
    private String ticket;
    private String requestContent;
    private String responseContent;

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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
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
                ", appKey='" + appKey + '\'' +
                ", createTime=" + createTime +
                ", ticket='" + ticket + '\'' +
                ", requestContent='" + requestContent + '\'' +
                ", responseContent='" + responseContent + '\'' +
                '}';
    }
}
