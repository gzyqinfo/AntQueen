package com.chetiwen.object;

import java.util.List;

public class CarResult {
    String lastTime;
    String vin;
    String type;
    String mileage;
    List<Material> materials;
    List<Detail> details;

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "CarResult{" +
                "lastTime='" + lastTime + '\'' +
                ", vin='" + vin + '\'' +
                ", type='" + type + '\'' +
                ", mileage='" + mileage + '\'' +
                ", materials=" + materials +
                ", details=" + details +
                '}';
    }
}
