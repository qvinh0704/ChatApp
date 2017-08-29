package com.example.quangvinh.chatapprx.Data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by QuangVinh on 3/29/2017.
 */

public class Message extends RealmObject{
    @PrimaryKey
    private long idRealm;
    private String id;
    private String content;
    private String status;
    private String receiverID;
    private String senderID;
    private String dialogID;

    public Message() {
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


    public String getDialogID() {
        return dialogID;
    }

    public void setDialogID(String dialogID) {
        this.dialogID = dialogID;
    }

    public long getIdRealm() {
        return idRealm;
    }

    public void setIdRealm(long idRealm) {
        this.idRealm = idRealm;
    }

    public void setReceiverID(String qbuserID) {
        receiverID = qbuserID;
    }

    public void setSenderID(String qbuserID) {
        senderID = qbuserID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getSenderID() {
        return senderID;
    }
}
