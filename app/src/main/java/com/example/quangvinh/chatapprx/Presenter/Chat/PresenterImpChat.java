package com.example.quangvinh.chatapprx.Presenter.Chat;

import android.os.Bundle;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

/**
 * Created by QuangVinh on 3/27/2017.
 */

public interface PresenterImpChat {
    public void initPresenterChat(Bundle bundle, EditText editText);
    public void getMessagesQuickblox(int pageSki);
    public void sendMessage(String content);
    public void getBundleFromFindUser(Bundle bundle);
    public ArrayList<MessageUI> getListMessageFromModel();
    public Integer getRecipientID();
    public void initMessageStatusListener();
    public void initTypingStatus();
    public void catchEventEditTextChat(EditText editText);
    public void showIsTyping();
    public void hideIsTyping();
    public void getMessagesRealm();
    public void loadMoreMessages();
    public void markReadMessage();
    public void createDialog();
    public void getDialog(String idSender, String idReceiver);
    public void setDialog(QBChatDialog qbChatDialog);
    public void loginChat();
}
