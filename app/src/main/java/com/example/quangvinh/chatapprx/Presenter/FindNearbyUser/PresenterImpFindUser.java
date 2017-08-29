package com.example.quangvinh.chatapprx.Presenter.FindNearbyUser;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by QuangVinh on 3/22/2017.
 */

public interface PresenterImpFindUser {
    public void getUserFromLogin(Bundle user);
    public void getUserFromUpdate(Bundle user);
    public void initPresenter(Bundle bundle);
    public void goToProfile();
    public void setGoogleMap(GoogleMap googleMap);
    public void initLocationService();
    public void setStatus(String status);
    public void retrieveUser();
    public void logOut();
    public void showProfileUser(String id);
    public void goToChat();
    public void goToChatHistory();
    public void pushNotification();
    public void showNewMessage();
    public void getUnreadMessage();
}
