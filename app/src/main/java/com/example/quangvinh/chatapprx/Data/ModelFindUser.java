package com.example.quangvinh.chatapprx.Data;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.quickblox.location.model.QBLocation;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by QuangVinh on 3/22/2017.
 */

public class ModelFindUser {
    private User mUser;
    private LatLng mLocation;
    private GoogleMap googleMap;
    private String status ="";
    private Map<String,QBLocation> listMarker;
    private User receiver;
    String receiverName;

    public ModelFindUser(){
        mUser = new User();
        listMarker = new HashMap<String,QBLocation>();
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public LatLng getmLocation() {
        return mLocation;
    }

    public void setmLocation(LatLng mLocation) {
        this.mLocation = mLocation;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, QBLocation> getListMarker() {
        return listMarker;
    }

    public void setListMarker(Map<String, QBLocation> listMarker) {
        this.listMarker = listMarker;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


}
