package com.chetiwen.object.qucent;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class QucentOrderData {

    private String time;
    private QucentOrderBasic basic;
    private QucentOrderResume resume;
    private List<QucentOrderRecord> mc;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public QucentOrderBasic getBasic() {
        return basic;
    }

    public void setBasic(QucentOrderBasic basic) {
        this.basic = basic;
    }

    public QucentOrderResume getResume() {
        return resume;
    }

    public void setResume(QucentOrderResume resume) {
        this.resume = resume;
    }

    public List<QucentOrderRecord> getMc() {
        return mc;
    }

    public void setMc(List<QucentOrderRecord> mc) {
        this.mc = mc;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
