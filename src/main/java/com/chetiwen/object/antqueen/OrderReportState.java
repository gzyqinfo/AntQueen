package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

public class OrderReportState {
    private int waterState;
    private int fireState;
    private int recallState;
    private int scrappedState;
    private int importantState;
    private int exteriorState;
    private int bodyPartState;

    public int getWaterState() {
        return waterState;
    }

    public void setWaterState(int waterState) {
        this.waterState = waterState;
    }

    public int getFireState() {
        return fireState;
    }

    public void setFireState(int fireState) {
        this.fireState = fireState;
    }

    public int getRecallState() {
        return recallState;
    }

    public void setRecallState(int recallState) {
        this.recallState = recallState;
    }

    public int getScrappedState() {
        return scrappedState;
    }

    public void setScrappedState(int scrappedState) {
        this.scrappedState = scrappedState;
    }

    public int getImportantState() {
        return importantState;
    }

    public void setImportantState(int importantState) {
        this.importantState = importantState;
    }

    public int getExteriorState() {
        return exteriorState;
    }

    public void setExteriorState(int exteriorState) {
        this.exteriorState = exteriorState;
    }

    public int getBodyPartState() {
        return bodyPartState;
    }

    public void setBodyPartState(int bodyPartState) {
        this.bodyPartState = bodyPartState;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
