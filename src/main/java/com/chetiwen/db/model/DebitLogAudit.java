package com.chetiwen.db.model;

import java.sql.Timestamp;

public class DebitLogAudit {
    private String operator;
    private Timestamp createTime;
    private String action;
    private String orderNo;
    private String vin;
    private String partnerId;
    private String brandId;
    private String brandName;
    private String balanceBeforeDebit;
    private String debitFee;
    private String feeType;
    private String timeUsedSec;

    public String getBalanceBeforeDebit() {
        return balanceBeforeDebit;
    }

    public String getDebitFee() {
        return debitFee;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setBalanceBeforeDebit(String balanceBeforeDebit) {
        this.balanceBeforeDebit = balanceBeforeDebit;
    }

    public void setDebitFee(String debitFee) {
        this.debitFee = debitFee;
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

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
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

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getTimeUsedSec() {
        return timeUsedSec;
    }

    public void setTimeUsedSec(String timeUsedSec) {
        this.timeUsedSec = timeUsedSec;
    }

    @Override
    public String toString() {
        return "DebitLogAudit{" +
                "operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", action='" + action + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", vin='" + vin + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", brandId='" + brandId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", balanceBeforeDebit='" + balanceBeforeDebit + '\'' +
                ", debitFee='" + debitFee + '\'' +
                ", feeType='" + feeType + '\'' +
                ", timeUsedSec=" + timeUsedSec +
                '}';
    }
}
