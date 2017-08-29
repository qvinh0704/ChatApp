package com.example.quangvinh.chatapprx.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by QuangVinh on 4/2/2017.
 */

public class ChatDialog extends RealmObject {
    @PrimaryKey
    private long idRealm;
    private String dialogID;
    private String senderID;
    private String receiverID;
    private boolean hasNewMessage;


    public ChatDialog() {

    }

    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }

    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    public long getIdRealm() {
        return idRealm;
    }

    public void setIdRealm(long idRealm) {
        this.idRealm = idRealm;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }
}
