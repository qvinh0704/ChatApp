package com.example.quangvinh.chatapprx.Presenter.FindNearbyUser;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.ModelFindUser;
import com.example.quangvinh.chatapprx.Data.Place;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperMap;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Presenter.Loading.LoadingActivity;
import com.example.quangvinh.chatapprx.Service.*;
import com.example.quangvinh.chatapprx.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.location.model.QBLocation;
import com.quickblox.users.model.QBUser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QuangVinh on 3/22/2017.
 */

public class PresenterFindUser implements PresenterImpFindUser, LocationListener {
    ModelFindUser modelFindUser;
    ViewFindUser mViewFindUser;
    Activity mActivity;
    QuickbloxService quickbloxService;
    FirebaseData mFirebaseData;
    SQLiteService sqLiteService;
    RealmService realmService;


    public PresenterFindUser(Activity activity, ViewFindUser viewFindUser) {
        mActivity = activity;
        mViewFindUser = viewFindUser;
        quickbloxService = QuickbloxService.getQuickblox_Instance(activity);
        modelFindUser = new ModelFindUser();
        mFirebaseData = new FirebaseData();
        sqLiteService = new SQLiteService(activity, Const.SQLITE_DATABASE_NAME, null, 1);
        Intent service = new Intent(activity.getBaseContext(), ListenMessageService.class);
        activity.startService(service);

        realmService = RealmService.getRealmService(activity);
    }

    @Override
    public void getUserFromLogin(Bundle user) {
        if (user != null) {
            User temp = (User) user.getSerializable(Const.BUNDLE_USER_FROM_LOGIN);
            if (temp != null) {
                temp.setQbuserID(quickbloxService.getQbUser().getId().toString());
                modelFindUser.setmUser(temp);
            }
        }
    }

    @Override
    public void getUserFromUpdate(Bundle user) {
        if (user != null) {
            User temp = (User) user.getSerializable(Const.BUNDLE_USER_FROM_UPDATE);
            if (temp != null) {
                modelFindUser.setmUser(temp);
            }
        }
    }

    @Override
    public void initPresenter(Bundle bundle) {
        if (ListenMessageService.bus != null)
            ListenMessageService.bus.register(this);

        getUserFromLogin(bundle);
        getUserFromUpdate(bundle);
        getUnreadMessage();


        //listen receiving message
//        quickbloxService.receivingIcomingMessages().subscribe(new Subscriber<QBChatMessage>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                LogUtil.error(e.getMessage());
//            }
//
//            @Override
//            public void onNext(QBChatMessage qbChatMessage) {
//                saveMessageToLocal(qbChatMessage);
//                bus.post(qbChatMessage);
//                if (Const.IS_VIBRATE)
//                    HelperDevice.vibrateDevice(mActivity);
//                showNewMessage();
//                pushNotification();
//                if (quickbloxService.getLastestMessages().size() == 0) {
//                    try {
//                        Badges.removeBadge(mActivity.getApplicationContext());
//                    } catch (BadgesNotSupportedException badgesNotSupportedException) {
//                        LogUtil.error(badgesNotSupportedException.getMessage());
//                    }
//                } else {
//                    int count = quickbloxService.getLastestMessages().size();
//                    try {
//                        Badges.setBadge(mActivity.getApplicationContext(), count);
//                    } catch (BadgesNotSupportedException badgesNotSupportedException) {
//                        LogUtil.error(badgesNotSupportedException.getMessage());
//                    }
//                }
//            }
//        });
    }

//    private void saveMessageToLocal(QBChatMessage qbChatMessage) {
//        Message message = HelperTransformation.changeQBChatMessageToMessageUI(qbChatMessage);
//        sqLiteService.addMessage(message);
//        quickbloxService.getLastestMessages().put(qbChatMessage.getDialogId(), qbChatMessage);
//    }

    @Override
    public void goToProfile() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_USER_FROM_FINDUSER, modelFindUser.getmUser());
        mViewFindUser.goToProfile(bundle);
    }

    @Override
    public void onLocationChanged(Location location) {
        modelFindUser.getGoogleMap().clear();
        LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        modelFindUser.getmUser().setLocation(new Place(mLocation.latitude, mLocation.longitude));
        if (modelFindUser.getmLocation() == null) {
            quickbloxService.createLocation(mLocation, modelFindUser.getStatus())
                    .flatMap(s -> mFirebaseData.updateUser(modelFindUser.getmUser(), null))
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.error(e.getMessage());
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {

                        }
                    });
        } else {
            quickbloxService.updateLocation(mLocation, modelFindUser.getStatus()).subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.error(e.getMessage());
                }

                @Override
                public void onNext(Boolean aBoolean) {

                }
            });
        }
        mViewFindUser.hideProgressbar();
        modelFindUser.setmLocation(mLocation);
        HelperMap.addMarker(mLocation, modelFindUser.getGoogleMap(), "You are here", BitmapDescriptorFactory.fromResource(R.drawable.pinkmarker));
        HelperMap.moveCamera(mLocation, modelFindUser.getGoogleMap(), 15);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void setGoogleMap(GoogleMap googleMap) {
        modelFindUser.setGoogleMap(googleMap);
    }

    @Override
    public void initLocationService() {
        mViewFindUser.showProgressbar();
        //init location service
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            LogUtil.debug("request permission runtime");
            return;
        }
        locationManager.requestLocationUpdates(Const.PROVIDER, 0, 50, this);
        mViewFindUser.showMessage("Check in");
    }

    @Override
    public void setStatus(String status) {
        modelFindUser.setStatus(status);
    }

    @Override
    public void retrieveUser() {
        mViewFindUser.showProgressbar();
        quickbloxService.retrieveLocation(modelFindUser.getmLocation(), 10)
                .subscribe(new Subscriber<ArrayList<QBLocation>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewFindUser.showMessage("Please check your connection");
                        mViewFindUser.hideProgressbar();
                    }

                    @Override
                    public void onNext(ArrayList<QBLocation> qbLocations) {
                        modelFindUser.getGoogleMap().clear();
                        modelFindUser.getListMarker().clear();
                        HelperMap.addMarker(modelFindUser.getmLocation(), modelFindUser.getGoogleMap(), "You are here", BitmapDescriptorFactory.fromResource(R.drawable.pinkmarker));
                        mViewFindUser.hideProgressbar();
                        QBUser instanceUser = quickbloxService.getQbUser();
                        boolean hasUsers = false;
                        for (int i = 0; i < qbLocations.size(); i++) {
                            if (!instanceUser.getId().equals(qbLocations.get(i).getUserId())) {
                                hasUsers = true;
                                LatLng location = new LatLng(qbLocations.get(i).getLatitude(), qbLocations.get(i).getLongitude());
                                Marker temp = HelperMap.addMarker(location, modelFindUser.getGoogleMap(), qbLocations.get(i).getStatus(), BitmapDescriptorFactory.fromResource(R.drawable.purplemarker));
                                modelFindUser.getListMarker().put(temp.getId(), qbLocations.get(i));
                            }
                        }
                        if (!hasUsers) {
                            HelperMap.moveCamera(modelFindUser.getmLocation(), modelFindUser.getGoogleMap(), 15);
                            mViewFindUser.showNoUser();
                        } else {
                            if (modelFindUser.getListMarker().size() == 1) {
                                mViewFindUser.showMessage(modelFindUser.getListMarker().size() + " user is nearby here");
                            } else {
                                mViewFindUser.showMessage(modelFindUser.getListMarker().size() + " users are nearby here");
                            }
                            HelperMap.moveCamera(modelFindUser.getmLocation(), modelFindUser.getGoogleMap(), 12);
                        }
                    }
                });
    }

    @Override
    public void logOut() {
//        Observable.just(sqLiteService.unmarkRememberUserDatabase(modelFindUser.getmUser().getEmail()))
        Observable.just(realmService.unmarkRememberUserDatabase(modelFindUser.getmUser().getEmail()))
                .flatMap(integer -> quickbloxService.logOutChatService())
                .flatMap(s -> quickbloxService.signOutQuickBlox())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.error(e.getMessage());
                        mViewFindUser.logout();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mViewFindUser.logout();
                    }
                });
//        sqLiteService.unmarkRememberUserDatabase(modelFindUser.getmUser().getEmail());
//        quickbloxService.logOutChatService()
//                .flatMap(s -> quickbloxService.signOutQuickBlox())
//                .subscribe(new Subscriber<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtil.error(e.getMessage());
//                        mViewFindUser.logout();
//                    }
//
//                    @Override
//                    public void onNext(Boolean aBoolean) {
//                        mViewFindUser.logout();
//                    }
//                });


    }

    @Override
    public void showProfileUser(String idMarker) {
        QBLocation qbLocation = modelFindUser.getListMarker().get(idMarker);
        if (qbLocation != null) {
            QBUser qbUser = qbLocation.getUser();
            if (qbUser != null) {
                mFirebaseData.getUserData(qbUser.getLogin(), null)
                        .subscribe(new Subscriber<User>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mViewFindUser.showMessage(e.getMessage());
                                LogUtil.error(e.getMessage());
                            }

                            @Override
                            public void onNext(User user) {
                                user.setStatus(qbLocation.getStatus());
                                mViewFindUser.showUserInfo(user);
                                modelFindUser.setReceiverName(user.getName());
                                user.setQbuserID(qbUser.getId().toString());
                                modelFindUser.setReceiver(user);
                            }
                        });
            }
        }
    }

    @Override
    public void goToChat() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_USER_FROM_FINDUSER, modelFindUser.getmUser());
        bundle.putSerializable(Const.BUNDLE_RECEIVER_FINDUSER, modelFindUser.getReceiver());
        mViewFindUser.goToChat(bundle);
    }

    @Override
    public void goToChatHistory() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_USER_FROM_FINDUSER, modelFindUser.getmUser());
        mViewFindUser.goToChatHistory(bundle);
    }

    @Override
    public void pushNotification() {
        NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(mActivity, LoadingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivity, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(mActivity)
                .setContentTitle(mActivity.getString(R.string.app_name))
                .setContentText("You have a new message")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.purplemarker)
                .build();
        notificationManager.notify(0, notification);
    }

    @Override
    public void showNewMessage() {
        int size = quickbloxService.getLastestMessages().size();
        mViewFindUser.showNotification(size);
    }

    @Subscribe
    public void getMessage(MessageUI messageUI) {
        showNewMessage();
    }

    @Override
    public void getUnreadMessage() {
        ArrayList<MessageUI> listUnreadMessage = realmService.getUnreadMessage(modelFindUser.getmUser().getQbuserID());
        for (int i = listUnreadMessage.size() - 1; i >= 0; i--) {
            MessageUI messageUI = listUnreadMessage.get(i);
            QBChatMessage qbChatMessage = HelperTransformation.changeMessageUI_To_QBChatMessage(messageUI);
            quickbloxService.getLastestMessages().put(qbChatMessage.getDialogId(), qbChatMessage);
        }
    }

}
