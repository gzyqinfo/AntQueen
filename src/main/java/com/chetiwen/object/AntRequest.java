package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;

public class AntRequest {
    private String partnerId;
    private int ts;
    private String sign;
    private String vin;
    private int specialBrand;
    private String callbackUrl;
    private String engineNum;
    private String licensePlate;
    private String channelId;
    private int isDetailReport;
    private String orderId;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getSpecialBrand() {
        return specialBrand;
    }

    public void setSpecialBrand(int specialBrand) {
        this.specialBrand = specialBrand;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(String engineNum) {
        this.engineNum = engineNum;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getIsDetailReport() {
        return isDetailReport;
    }

    public void setIsDetailReport(int isDetailReport) {
        this.isDetailReport = isDetailReport;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
