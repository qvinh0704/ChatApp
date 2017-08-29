package com.example.quangvinh.chatapprx.Data;

/**
 * Created by QuangVinh on 4/24/2017.
 */

public class ChatDialogUI {
    private String dialogID;
    private User sender;
    private User receiver;
    private boolean hasNewMessage;

    public ChatDialogUI() {
    }

    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }
}
