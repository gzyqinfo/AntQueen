package com.chetiwen.object;

public class CarResponse {
    String code;
    String msg;
    RetData data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RetData getData() {
        return data;
    }

    public void setData(RetData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CarResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
