package com.example.quangvinh.chatapprx.Presenter.FindNearbyUser;

import android.os.Bundle;

import com.example.quangvinh.chatapprx.Data.User;

/**
 * Created by QuangVinh on 3/22/2017.
 */

public interface ViewFindUser {
    public void goToProfile(Bundle bundle);
    public void showMessage(String message);
    public void showNoUser();
    public void showProgressbar();
    public void hideProgressbar();
    public void logout();
    public void showUserInfo(User user);
    public void goToChat(Bundle bundle);
    public void showNotification(int count);
    public void goToChatHistory(Bundle bundle);
    public void goToSetting();
}
