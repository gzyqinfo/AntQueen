package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

public class OrderReportResponse {

    private String msg;
    private int code;
    private OrderReportData data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OrderReportData getData() {
        return data;
    }

    public void setData(OrderReportData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
