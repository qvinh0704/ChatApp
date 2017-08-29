package com.example.quangvinh.chatapprx.Presenter.Login;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Service.FirebaseAuthentication;
import com.example.quangvinh.chatapprx.Service.FirebaseData;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.example.quangvinh.chatapprx.Service.RealmService;
import com.example.quangvinh.chatapprx.Service.SQLiteService;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by QuangVinh on 3/17/2017.
 */

public class PresenterLogin implements PresenterImpLogin {
    Activity mActivity;
    ViewLogin mViewLogin;
    FirebaseAuthentication mFirebaseAuthentication;
    FirebaseData mFirebaseData;
    QuickbloxService mQuickbloxService;
    SQLiteService sqLiteService;
    User mUser;
    RealmService realmService;

    public PresenterLogin(Activity activity, ViewLogin viewLogin) {
        mActivity = activity;
        mViewLogin = viewLogin;
        mFirebaseAuthentication = new FirebaseAuthentication(activity);
        mFirebaseData = new FirebaseData();
        mQuickbloxService = QuickbloxService.getQuickblox_Instance(activity);
        mUser = new User();
        sqLiteService = new SQLiteService(activity, Const.SQLITE_DATABASE_NAME, null, 1);
        realmService = RealmService.getRealmService(activity);
    }

    @Override
    public void initPresenterLogin(EditText edtUsername, EditText edtPassword, Bundle bundle) {
        validateButtonLogin(edtUsername, edtPassword);
        getBundleFromRegister(bundle);
    }

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void signin(User user, boolean rememberUser) {
        mViewLogin.hideTextViewSendVerification();
        mViewLogin.disableInputField();
        mViewLogin.disableButtonLogin();
        mViewLogin.showProgressbar();
        mViewLogin.showSigninError("");


        mQuickbloxService.createSessionQuickblox()
                .flatMap(s -> mFirebaseAuthentication.signinUserFirebase(user))
                .flatMap(s -> mFirebaseData.getUserData(user.getEmail(), mUser))
                .flatMap(us -> mQuickbloxService.signinQuickblox(mUser))
                .flatMap(us -> mQuickbloxService.loginToChat())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewLogin.showSigninError(e.getMessage());
                        LogUtil.error("Error signin-- " + e.getMessage());
                        mViewLogin.enableButtonLogin();
                        mViewLogin.enableInpuField();
                        mViewLogin.hideProgressbar();
                        if (e.getMessage().equals("Email is not verified")) {
                            mViewLogin.showTextViewSendVerification();
                        }
                        LogUtil.error("onError");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (rememberUser) {
                            markUserLastLogin();
                        }
                        Bundle bundleUser = new Bundle();
                        bundleUser.putSerializable(Const.BUNDLE_USER_FROM_LOGIN, mUser);
                        mViewLogin.showSigninSuccess(mUser.getName());
                        mViewLogin.goToFindUser(bundleUser);
                    }
                });

    }


    @Override
    public void resetPassword(String email) {
        mViewLogin.showSigninError("");
        if (TextUtils.isEmpty(email)) {
            try {
                mViewLogin.showSigninError("Email is empty");
                mViewLogin.closeDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mFirebaseAuthentication.resetPassword(email)
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            mViewLogin.showSigninError(e.getMessage());
                            mViewLogin.closeDialog();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            mViewLogin.showSendForgetSucess();
                            mViewLogin.closeDialog();
                        }
                    });
        }
    }

    @Override
    public void goToRegister() {
        mViewLogin.goToRegister();
    }

    @Override
    public void getBundleFromRegister(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.getSerializable(Const.BUNDLE_USER_FROM_REGISTER);
            if (user != null) {
                mViewLogin.setEmailField(user.getEmail());
                mViewLogin.setPasswordField(user.getPassword());
            }
        }
    }

    @Override
    public void validateButtonLogin(EditText edtUsername, EditText edtPassword) {
        Observable<CharSequence> observableUsername = RxTextView.textChanges(edtUsername);
        Observable<CharSequence> observablePassword = RxTextView.textChanges(edtPassword);
        Subscription subscription = Observable.combineLatest(observableUsername, observablePassword, new Func2<CharSequence, CharSequence, Boolean>() {

            @Override
            public Boolean call(CharSequence charSequence, CharSequence charSequence2) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches() && charSequence2.length() >= Const.MIN_PASSWORD_LENGTH)
                    return true;
                return false;
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
                    mViewLogin.enableButtonLogin();
                } else {
                    mViewLogin.disableButtonLogin();
                }
            }
        });
    }

    @Override
    public void showDialogResetPassword() {
        mViewLogin.showDialogForget();
    }

    @Override
    public void sendVerification() {
        mViewLogin.hideTextViewSendVerification();
        mViewLogin.showSigninError("");
        mFirebaseAuthentication.sendVerify(mFirebaseAuthentication.getCurrentUser()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mViewLogin.showSigninError(e.getMessage());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mViewLogin.showSendVerificationSuccessfully();


            }
        });
    }

    @Override
    public void markUserLastLogin() {
        mUser.setLastestLogin(1);
//        if (sqLiteService.checkUserExistByEmail(mUser.getEmail())) {
        if (realmService.checkUserExistByEmail(mUser.getEmail())) {
            try {
//                sqLiteService.markRememberUserDatabase(mUser.getEmail());
                realmService.markRememberUserDatabase(mUser.getEmail());
            } catch (SQLiteException e) {
                mViewLogin.showSigninError(e.getMessage());
            }
        } else {
            mUser.setQbuserID(mQuickbloxService.getQbUser().getId().toString());
//                sqLiteService.addUser(mUser);

            realmService.addUser(mUser);
        }
    }
}
