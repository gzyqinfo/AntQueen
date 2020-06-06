package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class AntRequest {
    private String partnerId;
    private int ts;
    private String sign;
    private String vin;
    private int specialBrand;

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


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
