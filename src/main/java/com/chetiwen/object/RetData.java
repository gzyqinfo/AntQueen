package com.chetiwen.object;

import java.util.List;

public class RetData {
    String orderNo;
    String brandName;
    String brandId;
    CarModel carModel;
    List<CarResult> result;
    String shareUrl;
    Object report;
    String appName;
    String userId;
    String vin;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public List<CarResult> getResult() {
        return result;
    }

    public void setResult(List<CarResult> result) {
        this.result = result;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public Object getReport() {
        return report;
    }

    public void setReport(Object report) {
        this.report = report;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "RetData{" +
                "orderNo='" + orderNo + '\'' +
                ", brandName='" + brandName + '\'' +
                ", brandId='" + brandId + '\'' +
                ", carModel=" + carModel +
                ", result=" + result +
                ", shareUrl='" + shareUrl + '\'' +
                ", report=" + report +
                ", appName='" + appName + '\'' +
                ", userId='" + userId + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
