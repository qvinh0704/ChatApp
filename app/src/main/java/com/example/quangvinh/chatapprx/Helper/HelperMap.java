package com.example.quangvinh.chatapprx.Helper;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by QuangVinh on 2/15/2017.
 */

public class HelperMap {

    public static PolylineOptions polylineOptions;
    public static Polyline line;

    public static LatLng searchDiaChi(String address, Activity mActivity) {
        Geocoder geocoder = new Geocoder(mActivity);
        List<Address> addressList;
        LatLng result = null;
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList == null || addressList.size() == 0) {
                return null;
            } else {
                Address location = addressList.get(0);
                result = new LatLng(location.getLatitude(), location.getLongitude());
            }
            Log.e("BBB", "Address " + address + " --Size " + address.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Marker addMarker(LatLng destination, GoogleMap tempMap, String nameMaker, BitmapDescriptor bitmapDescriptor) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (bitmapDescriptor != null) {
            markerOptions.icon(bitmapDescriptor);
        }
        markerOptions.position(destination);
        markerOptions.title(nameMaker);
        return tempMap.addMarker(markerOptions);
    }

    public static void moveCamera(LatLng destionation, GoogleMap tempMap, int zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destionation, zoom);
        tempMap.moveCamera(cameraUpdate);
    }
}
