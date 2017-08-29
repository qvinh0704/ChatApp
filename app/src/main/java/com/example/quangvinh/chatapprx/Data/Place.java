package com.example.quangvinh.chatapprx.Data;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by QuangVinh on 3/24/2017.
 */

public class Place extends RealmObject implements Serializable {
    private double lat;
    private double lng;
    public Place(){

    }
    public Place(double Lat, double Lng){
        lat =Lat;
        lng = Lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
