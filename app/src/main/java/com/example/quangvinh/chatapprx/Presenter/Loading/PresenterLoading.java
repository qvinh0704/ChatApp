package com.example.quangvinh.chatapprx.Presenter.Loading;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.example.quangvinh.chatapprx.Service.RealmService;
import com.example.quangvinh.chatapprx.Service.SQLiteService;
import com.quickblox.users.model.QBUser;

import java.io.File;

import rx.Subscriber;

/**
 * Created by QuangVinh on 3/20/2017.
 */

public class PresenterLoading implements PresenterImpLoading {
    Activity mActivity;
    ViewLoading mViewLoading;
    SQLiteService sqLiteService;
    RealmService realmService;
    User mUser;
    QuickbloxService mQuickbloxService;

    public PresenterLoading(Activity tempActivity, ViewLoading tempViewLoading) {
        mActivity = tempActivity;
        mViewLoading = tempViewLoading;
        sqLiteService = new SQLiteService(tempActivity, Const.SQLITE_DATABASE_NAME, null, 1);
        mQuickbloxService = QuickbloxService.getQuickblox_Instance(tempActivity);
        realmService = RealmService.getRealmService(tempActivity);
    }

    @Override
    public boolean checkExistDatabase() {
        File database = mActivity.getDatabasePath(Const.SQLITE_DATABASE_NAME);
        if (database.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void initPresenter() {
        if (!checkExistDatabase()) {
            sqLiteService.createTableUser();
        }
        mQuickbloxService.getLastestMessages().clear();
        loginLastestUser();
    }

    @Override
    public void loginLastestUser() {
//        mUser = sqLiteService.getUserLastLogin();
        mUser = realmService.getUserLastLogin();
        if (mUser != null) {
            String login = mUser.getEmail();
            mUser.setLoginQuickblox(login);
            QBUser qbUser = new QBUser();
            qbUser.setLogin(mUser.getEmail());
            qbUser.setPassword(mUser.getPassQuickblox());
            qbUser.setId(Integer.valueOf(mUser.getQbuserID()));
            mQuickbloxService.setQbUser(qbUser);
            Bundle bundleUser = new Bundle();
            bundleUser.putSerializable(Const.BUNDLE_USER_FROM_LOGIN, mUser);
            mQuickbloxService.loginToChat().subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mViewLoading.goToFindUser(bundleUser);
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    mViewLoading.goToFindUser(bundleUser);
                }
            });
        } else {
            new CountDownTimer(3000, 300) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mViewLoading.goToLogin();
                }
            }.start();

        }
    }
}
