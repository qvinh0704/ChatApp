package com.example.quangvinh.chatapprx.Presenter.Login;

import android.os.Bundle;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.User;

/**
 * Created by QuangVinh on 3/17/2017.
 */

public interface PresenterImpLogin {
    public void signin(User user, boolean rememberUser);
    public void resetPassword(String email);
    public void goToRegister();
    public void getBundleFromRegister(Bundle bundle);
    public void validateButtonLogin(EditText edtUsername, EditText edtPassword);
    public void initPresenterLogin(EditText edtUsername, EditText edtPassword,Bundle bundle);
    public void showDialogResetPassword();
    public void markUserLastLogin();
    public void sendVerification();
}
