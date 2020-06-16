package com.chetiwen.db.model;

public class OrderCallback {
    private String url;
    private String orderNo;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "OrderCallback{" +
                "url='" + url + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}
