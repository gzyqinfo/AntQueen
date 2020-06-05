package com.chetiwen.db.model;

import java.sql.Date;

public class User {
    private String userName;
    private String appKey;
    private int hidePassword;
    private float balance;
    private Date createDate;
    private int isValid;
    private float userFee;


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

    public int getHidePassword() {
        return hidePassword;
    }

    public void setHidePassword(int hidePassword) {
        this.hidePassword = hidePassword;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public float getUserFee() {
        return userFee;
    }

    public void setUserFee(float userFee) {
        this.userFee = userFee;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", appKey='" + appKey + '\'' +
                ", hidePassword=" + hidePassword +
                ", balance=" + balance +
                ", createDate=" + createDate +
                ", isValid=" + isValid +
                ", userFee=" + userFee +
                '}';
    }
}
