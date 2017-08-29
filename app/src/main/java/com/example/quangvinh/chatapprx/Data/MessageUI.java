package com.example.quangvinh.chatapprx.Data;

/**
 * Created by QuangVinh on 4/24/2017.
 */

public class MessageUI {
    private String id;
    private String content;
    private String status;
    private User receiver;
    private User sender;
    private String dialogID;

    public MessageUI() {
        receiver = new User();
        sender = new User();
        status = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }
}
