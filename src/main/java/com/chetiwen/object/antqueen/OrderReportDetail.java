package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

public class OrderReportDetail {
    private String date;
    private String positionName;
    private int positionId;
    private String abnormalWord;
    private String id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getAbnormalWord() {
        return abnormalWord;
    }

    public void setAbnormalWord(String abnormalWord) {
        this.abnormalWord = abnormalWord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
