package com.chetiwen.object;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * @version 1.0
 * @author: zwg.BlueOcean
 * @date 2018/3/1 18:28
 * @description
 */
public class PackBody {
    /**
     * 请求业务参数JSON串
     */
    private Object body;
    /**
     * 版本，默认1.0
     */
    private int version;
    /**
     * 时间戳
     */
    private int timestamp;
    /**
     * 请求流水号
     */
    private String ticket;
    /**
     * 对接方账号
     */
    private String appKey;
    /**
     * 签名，md5
     */
    private String sign;


    @JSONField(serialize = false)
    private String appSecret;


    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {

        this.body = body;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

