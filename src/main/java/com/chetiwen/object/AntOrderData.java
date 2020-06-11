package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class AntOrderData {

    private int orderId;
    private String mobilUrl;
    private String pcUrl;
    private List<AntOrderResult> records;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getMobilUrl() {
        return mobilUrl;
    }

    public void setMobilUrl(String mobilUrl) {
        this.mobilUrl = mobilUrl;
    }

    public String getPcUrl() {
        return pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    public List<AntOrderResult> getRecords() {
        return records;
    }

    public void setRecords(List<AntOrderResult> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
