package com.example.quangvinh.chatapprx.Presenter.ChatHistory;

import android.os.Bundle;

import com.example.quangvinh.chatapprx.Data.ChatDialogUI;

import java.util.ArrayList;

/**
 * Created by QuangVinh on 3/30/2017.
 */

public interface PresenterImpChatHistory {
    public ArrayList<ChatDialogUI> getListChatDialogs();
    public void getDialogsFromQuickblox();
    public void markNewDialog();
    public void goToChat(int position);
    public void initPresenter(Bundle bundle);
    public String getMyQbUserId();
    public void getBundleFromFindUser(Bundle bundle);
}
