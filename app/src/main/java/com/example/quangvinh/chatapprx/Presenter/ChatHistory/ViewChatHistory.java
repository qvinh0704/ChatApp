package com.example.quangvinh.chatapprx.Presenter.ChatHistory;

import android.os.Bundle;

/**
 * Created by QuangVinh on 3/30/2017.
 */

public interface ViewChatHistory {
    public void refreshRecyclerView();
    public void goToHistory(Bundle bundle);
    public void showProgressbar();
    public void hideProgressbar();
    public void showNoConversation();
    public void hideNoConversation();
}
