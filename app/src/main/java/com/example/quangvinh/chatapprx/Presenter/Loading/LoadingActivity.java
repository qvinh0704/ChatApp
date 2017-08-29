package com.example.quangvinh.chatapprx.Presenter.Loading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.quangvinh.chatapprx.Helper.HelperDevice;
import com.example.quangvinh.chatapprx.Presenter.FindNearbyUser.FindUserActivity;
import com.example.quangvinh.chatapprx.Presenter.Login.LoginActivity;
import com.example.quangvinh.chatapprx.R;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

public class LoadingActivity extends AppCompatActivity implements ViewLoading {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
        PresenterLoading presenterLoading = new PresenterLoading(this, this);
        presenterLoading.initPresenter();
        HelperDevice.initialVibrate(getApplicationContext());
    }

    @Override
    public void goToLogin() {
            Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
    }

    @Override
    public void goToFindUser(Bundle bundle) {
        Intent intent = new Intent(LoadingActivity.this, FindUserActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
