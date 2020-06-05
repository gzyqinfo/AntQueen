package com.chetiwen.db.model;

import java.sql.Timestamp;

public class DebitLog {
    private int logId;
    private Timestamp createTime;
    private String orderNo;
    private String vin;
    private String userName;
    private String appKey;
    private String brandId;
    private String brandName;
    private float balanceBeforeDebit;
    private float debitFee;


    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public float getBalanceBeforeDebit() {
        return balanceBeforeDebit;
    }

    public void setBalanceBeforeDebit(float balanceBeforeDebit) {
        this.balanceBeforeDebit = balanceBeforeDebit;
    }

    public float getDebitFee() {
        return debitFee;
    }

    public void setDebitFee(float debitFee) {
        this.debitFee = debitFee;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "DebitLog{" +
                "logId=" + logId +
                ", createTime=" + createTime +
                ", orderNo='" + orderNo + '\'' +
                ", vin='" + vin + '\'' +
                ", userName='" + userName + '\'' +
                ", appKey='" + appKey + '\'' +
                ", brandId='" + brandId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", balanceBeforeDebit=" + balanceBeforeDebit +
                ", debitFee=" + debitFee +
                '}';
    }
}
