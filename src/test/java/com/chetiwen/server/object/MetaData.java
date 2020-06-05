package com.chetiwen.server.object;

public class MetaData {

    String vin;
    String callBackUrl;
    String brandId;

    String enginNum;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getEnginNum() {
        return enginNum;
    }

    public void setEnginNum(String enginNum) {
        this.enginNum = enginNum;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "vin='" + vin + '\'' +
                ", callBackUrl='" + callBackUrl + '\'' +
                ", brandId='" + brandId + '\'' +
                ", enginNum='" + enginNum + '\'' +
                '}';
    }
}
