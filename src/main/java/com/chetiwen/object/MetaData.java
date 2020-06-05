package com.chetiwen.object;

public class MetaData {
    private String orderNo;
    private String vin;
    private String callBackUrl;
    private String brandId;
    private String enginNum;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

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
                "orderNo='" + orderNo + '\'' +
                ", vin='" + vin + '\'' +
                ", callBackUrl='" + callBackUrl + '\'' +
                ", brandId='" + brandId + '\'' +
                ", enginNum='" + enginNum + '\'' +
                '}';
    }
}
