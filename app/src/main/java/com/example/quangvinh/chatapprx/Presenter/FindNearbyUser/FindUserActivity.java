package com.example.quangvinh.chatapprx.Presenter.FindNearbyUser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Presenter.Chat.ChatActivity;
import com.example.quangvinh.chatapprx.Presenter.ChatHistory.ChatHistory;
import com.example.quangvinh.chatapprx.Presenter.UpdateUser.UpdateUserActivity;
import com.example.quangvinh.chatapprx.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindUserActivity extends AppCompatActivity implements ViewFindUser, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    @BindView(R.id.toolbar_FindUser)
    Toolbar toolbar;
    @BindView(R.id.imageButtonCheckin_FindUser)
    ImageButton imageButtonCheckin;
    @BindView(R.id.imageButton_RetrieveUser)
    ImageButton imageButtonRetrieveUser;
    @BindView(R.id.progressBar_FindUser)
    ProgressBar progressBar;
    SlidingUpPanelLayout slidingPaneLayout;
    @BindView(R.id.imageViewAvatar_FindUser)
    ImageView avtar;
    @BindView(R.id.textViewName_FindUser)
    TextView tvName;
    @BindView(R.id.textViewSex_FindUser)
    TextView tvSex;
    @BindView(R.id.buttonChat_FindUser)
    Button btnChat;
    @BindView(R.id.activity_find_user)
    LinearLayout linearLayout;
    @BindView(R.id.textView_Notification)
    TextView tvNotification;
    @BindView(R.id.imageButtonChatHistory_FindUser)
    ImageButton imageButtonChatHistory;
    @BindView(R.id.editTextStatus_FindUser)
    EditText edtStatus;
    @BindView(R.id.textViewStatus_FindUser)
    TextView tvStatus;

    PresenterFindUser presenterFindUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        ButterKnife.bind(this);
        initView();
        presenterFindUser = new PresenterFindUser(this, this);
        Intent intent = getIntent();
        presenterFindUser.initPresenter(intent.getExtras());
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        slidingPaneLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mapFragment.getMapAsync(this);
        imageButtonCheckin.setOnClickListener(this);
        imageButtonRetrieveUser.setOnClickListener(this);
        imageButtonChatHistory.setOnClickListener(this);
        btnChat.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finduser, menu);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonCheckin_FindUser: {
                presenterFindUser.setStatus(edtStatus.getText().toString().trim());
                presenterFindUser.initLocationService();
            }
            break;
            case R.id.imageButton_RetrieveUser: {
                presenterFindUser.retrieveUser();
            }
            break;
            case R.id.buttonChat_FindUser: {
                presenterFindUser.goToChat();
            }
            break;
            case R.id.imageButtonChatHistory_FindUser: {
                tvNotification.setVisibility(View.GONE);
                presenterFindUser.goToChatHistory();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_logout: {
                presenterFindUser.logOut();
            }
            break;
            case R.id.menu_item_profile: {
                presenterFindUser.goToProfile();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToProfile(Bundle bundle) {
        Intent intent = new Intent(FindUserActivity.this, UpdateUserActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        presenterFindUser.setGoogleMap(googleMap);
        presenterFindUser.initLocationService();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.debug("permission is accept");
                presenterFindUser.initLocationService();
            } else {
                LogUtil.debug("permission is denied");
                Toast.makeText(getApplicationContext(), "Permission is denied. Can not check in your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoUser() {
        Toast.makeText(getApplicationContext(), "No one is nearby", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setEnabled(false);
    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
        linearLayout.setEnabled(true);
    }

    @Override
    public void logout() {
        finish();
        LogUtil.debug("finish app");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        presenterFindUser.showProfileUser(marker.getId());
        return false;
    }

    @Override
    public void showUserInfo(User user) {
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        tvSex.setText("Sex: " + user.getSex());
        tvName.setText("Full name: " + user.getName());
        tvStatus.setText("Status: "+user.getStatus());
        LogUtil.debug("Full name: " + user.getName() + "Sex: " + user.getSex());
        Picasso.with(this).load(user.getUrlImage()).placeholder(R.drawable.noimage).into(avtar);
    }

    @Override
    public void goToChat(Bundle bundle) {
        Intent intent = new Intent(FindUserActivity.this, ChatActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showNotification(int count) {
        if(count != 0){
            tvNotification.setVisibility(View.VISIBLE);
            tvNotification.setText(count+"");
        }
    }


    @Override
    public void goToChatHistory(Bundle bundle) {
        Intent intent = new Intent(FindUserActivity.this, ChatHistory.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        String panelState = slidingPaneLayout.getPanelState().toString();
        if(panelState.equals(SlidingUpPanelLayout.PanelState.EXPANDED.toString())){
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterFindUser.showNewMessage();
    }

    @Override
    public void goToSetting() {

    }
}
