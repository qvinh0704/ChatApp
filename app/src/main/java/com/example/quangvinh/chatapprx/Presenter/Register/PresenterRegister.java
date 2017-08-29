package com.example.quangvinh.chatapprx.Presenter.Register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Service.FirebaseAuthentication;
import com.example.quangvinh.chatapprx.Service.FirebaseData;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func3;

/**
 * Created by QuangVinh on 3/15/2017.
 */

public class PresenterRegister implements PresenterImpRegister {
    Activity mActivity;
    ViewRegister mViewRegister;
    FirebaseData mFirebaseData;
    FirebaseAuthentication mFirebaseAuthentication;
    QuickbloxService mQuickbloxService;
    File avatar;
    User user;

    public PresenterRegister(Activity activity, ViewRegister viewRegister) {
        mActivity = activity;
        mViewRegister = viewRegister;
        mFirebaseAuthentication = new FirebaseAuthentication(activity);
        mFirebaseData = new FirebaseData();
        mQuickbloxService = QuickbloxService.getQuickblox_Instance(mActivity);
    }
    @Override
    public void validateButtonRegister(EditText edtEmail, EditText edtFullname, EditText edtPassword) {
        Observable<CharSequence> observableEmail = RxTextView.textChanges(edtEmail);
        Observable<CharSequence> observableFullname = RxTextView.textChanges(edtFullname);
        Observable<CharSequence> observablePassword = RxTextView.textChanges(edtPassword);


        Subscription subscription = Observable.combineLatest(observableEmail, observableFullname, observablePassword, new Func3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches() && charSequence2.length() >= Const.MIN_FULLNAME_LENGTH && charSequence3.length() >= Const.MIN_PASSWORD_LENGTH)
                    return true;
                return false;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    mViewRegister.enableButtonRegister();
                } else {
                    mViewRegister.disableButtonRegister();
                }
            }
        });
    }

    @Override
    public void signUp(User tempUser) {
        user = tempUser;
        mViewRegister.showRegisterFailed("");
        mViewRegister.showProgressbar();
        mViewRegister.disableButtonRegister();
        mViewRegister.disableInputField();

        mQuickbloxService.createSessionQuickblox()
                .flatMap(s -> mFirebaseAuthentication.signupFirebase(user))
                .flatMap(s -> mFirebaseAuthentication.sendVerify(s))
                .flatMap(s -> mQuickbloxService.requestSignupQuickblox(user))
                .flatMap(s -> mQuickbloxService.signinQuickblox(user))
                .flatMap(s -> mQuickbloxService.upLoadAvatar(avatar))
                .flatMap(file -> mFirebaseData.addUser(user, file))
                .flatMap(s -> mQuickbloxService.updateUserProfile())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        processError(e.getMessage());
                        refreshForm();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        mViewRegister.showRegisterSucess();
                        goToLogin();
                    }
                });
    }

    private void processError(String message) {
        switch (message) {
            case Const.ERROR_SESSION_QUICKBLOX:
            case Const.ERROR_SIGNUP_FIREBASE: {
                mViewRegister.showErrorNetworkConnection();
                refreshForm();
            }
            break;
            case Const.ERROR_SENDVERIFY_FIREBASE:
            case Const.ERROR_SIGNUP_QUICKBLOX:{
                mFirebaseAuthentication.signinUserFirebase(user)
                        .flatMap(u -> mFirebaseAuthentication.deleteUser_Firebase(u))
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mViewRegister.showErrorNetworkConnection();
                                refreshForm();
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                refreshForm();
                            }
                        });
            }
            break;
            case Const.ERROR_SIGNIN_QUICKBLOX:
            case Const.ERROR_UPLOADAVATAR_QUICKBLOX:
            case Const.ERROR_ADDUSER_FIREBASE:
            case Const.ERROR_UPDATE_QUICKBLOX:{
                mFirebaseAuthentication.signinUserFirebase(user)
                        .flatMap(u -> mFirebaseAuthentication.deleteUser_Firebase(u))
                        .flatMap(u -> mQuickbloxService.deleteUser_Quickblox())
                        .subscribe(new Subscriber<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mViewRegister.showErrorNetworkConnection();
                                refreshForm();
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                refreshForm();
                            }
                        });
            }
            break;
            default:{
                mViewRegister.showRegisterFailed(message);
            }break;
        }
    }

    private void refreshForm(){
        mViewRegister.hideProgressbar();
        mViewRegister.enableInputField();
        mViewRegister.enableButtonRegister();
        mViewRegister.clearPassword();
    }

    @Override
    public void setFileAvatar(Bitmap bitmap) {
//        try {
//            Bitmap bmScale = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
//            avatar = new File(mActivity.getCacheDir(), String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpeg");
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bmScale.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
//            byte[] bytes = byteArrayOutputStream.toByteArray();
//            FileOutputStream fileOutputStream = new FileOutputStream(avatar);
//            fileOutputStream.write(bytes);
//            fileOutputStream.flush();
//            fileOutputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            mViewRegister.showRegisterFailed(e.getMessage());
//        } catch (IOException e) {
//            e.printStackTrace();
//            mViewRegister.showRegisterFailed(e.getMessage());
//        }
        try {
            avatar = HelperTransformation.changeBitmapToFile(bitmap,mActivity);
        } catch (IOException e) {
            mViewRegister.showRegisterFailed(e.getMessage());
            LogUtil.error(e.getMessage());
        }
    }

    @Override
    public void intentToBitmap(Intent intent) {
        try {
            Bitmap bitmap = HelperTransformation.intentToBitmap(intent,mActivity);
            setFileAvatar(bitmap);
            mViewRegister.setAvatarToImageview(bitmap);
        } catch (FileNotFoundException e) {
            mViewRegister.showRegisterFailed(e.getMessage());
            LogUtil.error(e.getMessage());
        }
//        Uri uri = intent.getData();
//        InputStream inputStream = null;
//        try {
//            inputStream = mActivity.getContentResolver().openInputStream(uri);
//            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//            setFileAvatar(bitmap);
//            mViewRegister.setAvatarToImageview(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            LogUtil.error(e.getMessage());
//        }
    }

    public String getSex(Boolean isMale) {
        if (isMale)
            return "Male";
        return "Female";
    }

    @Override
    public void goToLogin() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_USER_FROM_REGISTER,user);
        mViewRegister.goToLogin(bundle);
    }
}
