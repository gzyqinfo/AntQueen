package com.chetiwen.object;

public class Detail {
    String type;
    String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
