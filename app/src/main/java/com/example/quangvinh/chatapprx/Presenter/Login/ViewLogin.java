package com.example.quangvinh.chatapprx.Presenter.Login;

import android.os.Bundle;

/**
 * Created by QuangVinh on 3/17/2017.
 */

public interface ViewLogin {
    public void goToRegister();
    public void enableInpuField();
    public void disableInputField();
    public void enableButtonLogin();
    public void disableButtonLogin();
    public void showSigninSuccess(String name);
    public void showSigninError(String messageError);
    public void showProgressbar();
    public void hideProgressbar();
    public void setEmailField(String email);
    public void setPasswordField(String password);
    public void showDialogForget();
    public void showSendForgetSucess();
    public void closeDialog();
    public void goToFindUser(Bundle user);
    public void showSendVerificationSuccessfully();
    public void showTextViewSendVerification();
    public void hideTextViewSendVerification();
}
