package com.example.quangvinh.chatapprx.Data;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by QuangVinh on 4/2/2017.
 */

public class ModelChatHistory {
    private ArrayList<ChatDialogUI> listChatDialog;
    private ArrayList<QBUser> listQBUsers;
    private User mUser;

    public ModelChatHistory() {
        listChatDialog = new ArrayList<ChatDialogUI>();
    }

    public ArrayList<ChatDialogUI> getListChatDialog() {
        return listChatDialog;
    }

    public void setListChatDialog(ArrayList<ChatDialogUI> listChatDialog) {
        this.listChatDialog = listChatDialog;
    }

    public ArrayList<QBUser> getListQBUsers() {
        return listQBUsers;
    }

    public void setListQBUsers(ArrayList<QBUser> listQBUsers) {
        this.listQBUsers = listQBUsers;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }
}
