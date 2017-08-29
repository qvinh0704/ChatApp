package com.example.quangvinh.chatapprx.Data;

import com.example.quangvinh.chatapprx.Helper.Const;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by QuangVinh on 3/27/2017.
 */

public class ModelChat {
    private QBChatDialog mQBChatDialog;
    private ArrayList<MessageUI> listMessage;
    private User receiver;
    private User mUser;
    private boolean isTyping;

    public ModelChat(){
        listMessage = new ArrayList<MessageUI>();
        isTyping = false;
    }

    public QBChatDialog getmQBChatDialog() {
        return mQBChatDialog;
    }

    public void setmQBChatDialog(QBChatDialog mQBChatDialog) {
        this.mQBChatDialog = mQBChatDialog;
    }

    public ArrayList<MessageUI> getListMessage() {
        return listMessage;
    }

    public void setListMessage(ArrayList<MessageUI> listMessage) {
        this.listMessage = listMessage;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public MessageUI getMessageByID(String id){
        int n = listMessage.size() - 1;
        for(int i = n ; i >= 0;i--){
            if(listMessage.get(i).getId().equals(id))
                return listMessage.get(i);
        }
        return null;
    }

    public void removeTyping_Message(){
        int n = listMessage.size() - 1;
        for(int i = n ; i >= (n/2);i--){
            if(listMessage.get(i).getId().equals(Const.ID_MESSAGE_TYPING))
                listMessage.remove(i);
        }
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public void addMessage(MessageUI message){
        listMessage.add(message);
    }

}
