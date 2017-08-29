package com.example.quangvinh.chatapprx.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.quangvinh.chatapprx.Presenter.Chat.PresenterChat;
import com.squareup.otto.Bus;

/**
 * Created by Vinh on 8/21/2017.
 */

public class WifiReceiver extends BroadcastReceiver {
    public static Bus bus = new Bus();
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            bus.post("YES");
        }else{
            bus.post("NO");
        }
    }
}
