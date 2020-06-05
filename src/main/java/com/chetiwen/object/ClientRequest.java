package com.chetiwen.object;

public class ClientRequest {
    private int timestamp;
    private String ticket;
    private String appKey;
    private String sign;
    private MetaData body;

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

    public MetaData getBody() {
        return body;
    }

    public void setBody(MetaData body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "timestamp=" + timestamp +
                ", ticket='" + ticket + '\'' +
                ", appKey='" + appKey + '\'' +
                ", sign='" + sign + '\'' +
                ", body=" + body +
                '}';
    }
}
