package com.chetiwen.db.model;

public class UserRate {
    private String partnerId;
    private float price;
    private String brandId;
    private String brandName;

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

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

    @Override
    public String toString() {
        return "UserRate{" +
                "partnerId='" + partnerId + '\'' +
                ", price=" + price +
                ", brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
