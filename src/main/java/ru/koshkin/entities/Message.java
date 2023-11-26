package ru.koshkin.entities;

public abstract class Message {
    protected Integer uid;
    protected String fromId;
    protected String toId;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "uid=" + uid +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                '}';
    }
}
