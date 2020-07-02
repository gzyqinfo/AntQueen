package com.chetiwen.db.model;

import java.sql.Timestamp;

public class Order {
    private String vin;
    private String orderNo;
    private String responseContent;
    private Timestamp createTime;
    private String dataSource;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String toString() {
        return "Order{" +
                "vin='" + vin + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", responseContent='" + responseContent + '\'' +
                ", createTime=" + createTime +
                ", dataSource='" + dataSource + '\'' +
                '}';
    }
}
