package com.chetiwen.object.antqueen;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class OrderReportData {
    private OrderReportPartDetail part_detail;
    private String brandName;
    private int carLevel;
    private int makeReportDate;
    private String reportUrl;
    private String mileageEstimate;
    private String lastMainTime;
    private String carAge;
    private String mileageEveryYear;
    private String vin;
    private OrderReportState state;
    private List<OrderReportMileage> mileage_data;
    private List<OrderReportRepairDetail> normalRepairRecords;
    private int reportNo;

    public OrderReportPartDetail getPart_detail() {
        return part_detail;
    }

    public void setPart_detail(OrderReportPartDetail part_detail) {
        this.part_detail = part_detail;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getCarLevel() {
        return carLevel;
    }

    public void setCarLevel(int carLevel) {
        this.carLevel = carLevel;
    }

    public int getMakeReportDate() {
        return makeReportDate;
    }

    public void setMakeReportDate(int makeReportDate) {
        this.makeReportDate = makeReportDate;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getMileageEstimate() {
        return mileageEstimate;
    }

    public void setMileageEstimate(String mileageEstimate) {
        this.mileageEstimate = mileageEstimate;
    }

    public String getLastMainTime() {
        return lastMainTime;
    }

    public void setLastMainTime(String lastMainTime) {
        this.lastMainTime = lastMainTime;
    }

    public String getCarAge() {
        return carAge;
    }

    public void setCarAge(String carAge) {
        this.carAge = carAge;
    }

    public String getMileageEveryYear() {
        return mileageEveryYear;
    }

    public void setMileageEveryYear(String mileageEveryYear) {
        this.mileageEveryYear = mileageEveryYear;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public OrderReportState getState() {
        return state;
    }

    public void setState(OrderReportState state) {
        this.state = state;
    }

    public List<OrderReportMileage> getMileage_data() {
        return mileage_data;
    }

    public void setMileage_data(List<OrderReportMileage> mileage_data) {
        this.mileage_data = mileage_data;
    }

    public List<OrderReportRepairDetail> getNormalRepairRecords() {
        return normalRepairRecords;
    }

    public void setNormalRepairRecords(List<OrderReportRepairDetail> normalRepairRecords) {
        this.normalRepairRecords = normalRepairRecords;
    }

    public int getReportNo() {
        return reportNo;
    }

    public void setReportNo(int reportNo) {
        this.reportNo = reportNo;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
