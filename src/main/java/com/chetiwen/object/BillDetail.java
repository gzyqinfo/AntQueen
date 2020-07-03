package com.chetiwen.object;

import java.sql.Timestamp;

public class BillDetail {
    private String userId;
    private String userName;
    private Timestamp timestamp;
    private String billType;
    private float amount;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BillDetail{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", billType='" + billType + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
