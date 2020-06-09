package com.chetiwen.db.model;

public class Brand {
    private String brandId;
    private String brandName;
    private float price;
    private String isSpecial;
    private String pingyinName;
    private String isEngine;

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getPingyinName() {
        return pingyinName;
    }

    public void setPingyinName(String pingyinName) {
        this.pingyinName = pingyinName;
    }

    public String getIsEngine() {
        return isEngine;
    }

    public void setIsEngine(String isEngine) {
        this.isEngine = isEngine;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "brandId='" + brandId + '\'' +
                ", brandName='" + brandName + '\'' +
                ", price=" + price +
                ", isSpecial='" + isSpecial + '\'' +
                ", pingyinName='" + pingyinName + '\'' +
                ", isEngine=" + isEngine +
                '}';
    }
}
