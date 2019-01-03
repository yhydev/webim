package priv.yanyang.webim.entity;

import java.util.Date;

public class Message  {


    private Long index;
    private String channel;
    private String content;
    private Date createTime;
    private String type;

    @Override
    public String toString() {
        return "Message{" +
                "index=" + index +
                ", channel='" + channel + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", type='" + type + '\'' +
                '}';
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
