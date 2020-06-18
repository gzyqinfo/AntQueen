package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

public class OrderReportMileage {
    private String date;
    private String mileage;
    private int status;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
