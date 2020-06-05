package com.chetiwen.server.object;

public class Request {
    long version;
    long timestamp;
    String ticket;
    String appKey;
    String sign;
    MetaData body;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
        return "Request{" +
                "version=" + version +
                ", timestamp=" + timestamp +
                ", ticket='" + ticket + '\'' +
                ", appKey='" + appKey + '\'' +
                ", sign='" + sign + '\'' +
                ", body=" + body +
                '}';
    }
}
