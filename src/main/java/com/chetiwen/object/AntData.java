package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;

public class AntData {
    private int isEngine;
    private int isLicensePlate;
    private int brandId;
    private String brandName;
    private String price;

    public int getIsEngine() {
        return isEngine;
    }

    public void setIsEngine(int isEngine) {
        this.isEngine = isEngine;
    }

    public int getIsLicensePlate() {
        return isLicensePlate;
    }

    public void setIsLicensePlate(int isLicensePlate) {
        this.isLicensePlate = isLicensePlate;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
