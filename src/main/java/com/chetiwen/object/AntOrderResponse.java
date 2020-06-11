package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;

public class AntOrderResponse {
    private int code;
    private String msg;
    private AntOrderData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public AntOrderData getData() {
        return data;
    }

    public void setData(AntOrderData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
