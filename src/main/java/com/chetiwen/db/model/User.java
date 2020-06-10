package com.chetiwen.db.model;

import java.sql.Date;

public class User {
    private String userName;
    private String partnerId;
    private String partnerKey;
    private float balance;
    private int isValid;

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

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", partnerKey='" + partnerKey + '\'' +
                ", balance=" + balance +
                ", isValid=" + isValid +
                '}';
    }
}
