package com.chetiwen.object.insurance;

public class InsResponse {
    String code;
    String msg;
    InsData data;

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

    public InsData getData() {
        return data;
    }

    public void setData(InsData data) {
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
