package com.chetiwen.db.model;

public class OrderMap {
    private String replaceOrderNo;
    private String orderNo;

    public String getReplaceOrderNo() {
        return replaceOrderNo;
    }

    public void setReplaceOrderNo(String replaceOrderNo) {
        this.replaceOrderNo = replaceOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "OrderMap{" +
                "replaceOrderNo='" + replaceOrderNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
