package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class OrderReportPartDetail {
    private List<OrderReportDetail> important;
    private List<OrderReportDetail> recall;
    private List<OrderReportDetail> exterior;
    private List<OrderReportDetail> fire;
    private List<OrderReportDetail> scrapped;
    private List<OrderReportDetail> water;
    private List<OrderReportDetail> body_part;

    public List<OrderReportDetail> getImportant() {
        return important;
    }

    public void setImportant(List<OrderReportDetail> important) {
        this.important = important;
    }

    public List<OrderReportDetail> getRecall() {
        return recall;
    }

    public void setRecall(List<OrderReportDetail> recall) {
        this.recall = recall;
    }

    public List<OrderReportDetail> getExterior() {
        return exterior;
    }

    public void setExterior(List<OrderReportDetail> exterior) {
        this.exterior = exterior;
    }

    public List<OrderReportDetail> getFire() {
        return fire;
    }

    public void setFire(List<OrderReportDetail> fire) {
        this.fire = fire;
    }

    public List<OrderReportDetail> getScrapped() {
        return scrapped;
    }

    public void setScrapped(List<OrderReportDetail> scrapped) {
        this.scrapped = scrapped;
    }

    public List<OrderReportDetail> getWater() {
        return water;
    }

    public void setWater(List<OrderReportDetail> water) {
        this.water = water;
    }

    public List<OrderReportDetail> getBody_part() {
        return body_part;
    }

    public void setBody_part(List<OrderReportDetail> body_part) {
        this.body_part = body_part;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
