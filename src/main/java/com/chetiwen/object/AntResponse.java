package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;

public class AntResponse {
    private int code;
    private String msg;
    private AntData data;

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

    public AntData getData() {
        return data;
    }

    public void setData(AntData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
