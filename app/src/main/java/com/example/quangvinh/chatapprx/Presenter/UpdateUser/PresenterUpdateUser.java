package com.example.quangvinh.chatapprx.Presenter.UpdateUser;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.ModelUpdateUser;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Service.FirebaseAuthentication;
import com.example.quangvinh.chatapprx.Service.FirebaseData;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.example.quangvinh.chatapprx.Service.RealmService;
import com.example.quangvinh.chatapprx.Service.SQLiteService;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.FileNotFoundException;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by QuangVinh on 3/20/2017.
 */

public class PresenterUpdateUser implements PresenterImpUpdateUser {
    Activity mActivity;
    ViewUpdateUser mViewUpdateUser;
    QuickbloxService mQuickbloxService;
    FirebaseData mFirebaseData;
    FirebaseAuthentication mFirebaseAuthentication;
    ModelUpdateUser modelUpdateUser;
    SQLiteService sqLiteService;
    Bitmap avatar;
    RealmService realmService;

    public PresenterUpdateUser(Activity activity, ViewUpdateUser viewUpdateUser) {
        mActivity = activity;
        mViewUpdateUser = viewUpdateUser;
        mFirebaseData = new FirebaseData();
        mFirebaseAuthentication = new FirebaseAuthentication(activity);
        mQuickbloxService = QuickbloxService.getQuickblox_Instance(activity);
        modelUpdateUser = new ModelUpdateUser();
        sqLiteService = new SQLiteService(activity, Const.SQLITE_DATABASE_NAME, null, 1);
        realmService = RealmService.getRealmService(activity);
    }

    @Override
    public void updateUser(User newUser) {
        mViewUpdateUser.closeDialog();
        mViewUpdateUser.disableButtonSave();
        mViewUpdateUser.disableInputField();
        mViewUpdateUser.showProgressbar();
        if (modelUpdateUser.isChangedAvatar()) {
            mFirebaseAuthentication.updateUser(newUser.getPassword())
                    .flatMap(s -> mQuickbloxService.upLoadAvatar(modelUpdateUser.getAvatar()))
                    .flatMap(s -> mFirebaseData.updateUser(newUser, s))
                    .flatMap(s -> mQuickbloxService.updateUserProfile())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mViewUpdateUser.showUpdateFailed(e.getMessage());
                            mViewUpdateUser.enableButtonSave();
                            mViewUpdateUser.enableInputField();
                            mViewUpdateUser.hideProgressbar();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            modelUpdateUser.setUser(newUser);
                            try {
//                                sqLiteService.updateUser(modelUpdateUser.getUser());
                                realmService.updateUser(modelUpdateUser.getUser());
                            } catch (SQLiteException e) {
                                mViewUpdateUser.showUpdateFailed(e.getMessage());
                            }
                            mViewUpdateUser.showUpdateUserSuccess();
                            mViewUpdateUser.enableButtonSave();
                            mViewUpdateUser.enableInputField();
                            mViewUpdateUser.hideProgressbar();
                            goToFindUser();
                        }
                    });
        }
        //Update no change avatar
        else {
            mFirebaseAuthentication.updateUser(newUser.getPassword())
                    .flatMap(s -> mFirebaseData.updateUser(newUser, null))
                    .flatMap(s -> mQuickbloxService.updateUserProfile())
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mViewUpdateUser.showUpdateFailed(e.getMessage());
                            mViewUpdateUser.enableButtonSave();
                            mViewUpdateUser.enableInputField();
                            mViewUpdateUser.hideProgressbar();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            modelUpdateUser.setUser(newUser);
                            try {
//                                sqLiteService.updateUser(modelUpdateUser.getUser());
                                realmService.updateUser(modelUpdateUser.getUser());
                            } catch (SQLiteException e) {
                                mViewUpdateUser.showUpdateFailed(e.getMessage());
                            }
                            mViewUpdateUser.showUpdateUserSuccess();
                            mViewUpdateUser.enableButtonSave();
                            mViewUpdateUser.enableInputField();
                            mViewUpdateUser.hideProgressbar();
                            goToFindUser();
                        }
                    });
        }

    }

    @Override
    public void validateButtonSave(EditText edtFullname, EditText edtPassword) {
        Observable<CharSequence> observableName = RxTextView.textChanges(edtFullname);
        Observable<CharSequence> observablePassword = RxTextView.textChanges(edtPassword);
        Subscription subscription = Observable.combineLatest(observableName, observablePassword, new Func2<CharSequence, CharSequence, Boolean>() {

            @Override
            public Boolean call(CharSequence charSequenceName, CharSequence charSequencePassword) {
                int lengthName = charSequenceName.length();
                int lengthPass = charSequencePassword.length();
                if (lengthName < Const.MIN_FULLNAME_LENGTH || lengthPass < Const.MIN_PASSWORD_LENGTH)
                    return false;
                return true;
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    mViewUpdateUser.enableButtonSave();
                } else {
                    mViewUpdateUser.disableButtonSave();
                }
            }
        });
    }

    @Override
    public void getUserFromFindUser(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.getSerializable(Const.BUNDLE_USER_FROM_FINDUSER);
            modelUpdateUser.setUser(user);
        } else {
            mViewUpdateUser.showGetProfileFailed();
        }
    }

    @Override
    public void loadProfile() {
        mViewUpdateUser.showProfileUser(modelUpdateUser.getUser());
    }

    @Override
    public void showDiaglogVerify() {
        mViewUpdateUser.showDiaglogVerify();
    }

    @Override
    public void requestUpdateUser(User newUser, String currPass) {
        User currentUser = modelUpdateUser.getUser();
        if (currentUser.getPassword().equals(currPass)) {
            newUser.setPassQuickblox(currentUser.getPassQuickblox());
            newUser.setEmail(currentUser.getEmail());
            newUser.setLoginQuickblox(currentUser.getLoginQuickblox());
            newUser.setUserID(currentUser.getUserID());
            newUser.setUrlImage(currentUser.getUrlImage());
            updateUser(newUser);
        } else {
            mViewUpdateUser.showVerifyUserFailed();
        }
    }

    @Override
    public void markAvatarChanged() {
        modelUpdateUser.setChangedAvatar(true);
    }

    @Override
    public void changeIntentToBitmap(Intent intent) {
        try {
            Bitmap bitmap = HelperTransformation.intentToBitmap(intent, mActivity);
            mViewUpdateUser.showAvatarToImageview(bitmap);
        } catch (FileNotFoundException e) {
            mViewUpdateUser.showUpdateFailed(e.getMessage());
        }
    }

    @Override
    public void setFileAvatar(Bitmap bitmap) {
        try {
            modelUpdateUser.setAvatar(HelperTransformation.changeBitmapToFile(bitmap, mActivity));
        } catch (IOException e) {
            mViewUpdateUser.showUpdateFailed(e.getMessage());
        }
    }

    @Override
    public void goToFindUser() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_USER_FROM_UPDATE, modelUpdateUser.getUser());
        mViewUpdateUser.goToFindUser(bundle);
    }
}
