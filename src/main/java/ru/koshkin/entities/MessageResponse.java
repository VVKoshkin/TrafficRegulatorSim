package ru.koshkin.entities;

public class MessageResponse extends Message {
    private String responseOption;
    private String responseDetailsOption;
    private String bookedBy;

    public MessageResponse(Integer uid, String fromId, String toId, String responseOption, String responseDetailsOption, String bookedBy) {
        this.uid = uid;
        this.fromId = fromId;
        this.toId = toId;
        this.responseOption = responseOption;
        this.responseDetailsOption = responseDetailsOption;
        this.bookedBy = bookedBy;
    }

    public String getResponseOption() {
        return responseOption;
    }

    public String getResponseDetailsOption() {
        return responseDetailsOption;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "uid=" + uid +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", responseOption='" + responseOption + '\'' +
                ", responseDetailsOption='" + responseDetailsOption + '\'' +
                ", bookedBy='" + bookedBy + '\'' +
                '}';
    }
}
