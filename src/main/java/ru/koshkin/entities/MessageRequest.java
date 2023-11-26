package ru.koshkin.entities;

import ru.koshkin.communication.RequestOptions;

public class MessageRequest extends Message {
    private Long querySize;

    public Long getQuerySize() {
        return querySize;
    }

    public String getOption() {
        return option;
    }

    private String option;

    public MessageRequest(Integer uid, String fromId, String toId, Long querySize, String option) {
        this.uid = uid;
        this.fromId = fromId;
        this.toId = toId;
        this.querySize = querySize;
        this.option = option;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "uid=" + uid +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", querySize=" + querySize +
                ", option='" + option + '\'' +
                '}';
    }
}
