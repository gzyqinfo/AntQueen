package com.chetiwen.object;

import com.chetiwen.db.model.DebitLog;

import java.util.List;

public class HistoricalOrders {
    String code;
    String msg;
    List<DebitLog> orders;

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

    public List<DebitLog> getOrders() {
        return orders;
    }

    public void setOrders(List<DebitLog> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "HistoricalOrders{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", orders=" + orders +
                '}';
    }
}
