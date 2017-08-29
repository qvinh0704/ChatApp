package com.example.quangvinh.chatapprx.Presenter.Register;

import android.graphics.Bitmap;
import android.os.Bundle;

/**
 * Created by QuangVinh on 3/15/2017.
 */

public interface ViewRegister {
    public void enableButtonRegister();
    public void disableButtonRegister();
    public void showProgressbar();
    public void hideProgressbar();
    public void enableInputField();
    public void disableInputField();
    public void showRegisterSucess();
    public void showRegisterFailed(String messageError);
    public void goToLogin(Bundle user);
    public void setAvatarToImageview(Bitmap bitmap);
    public void showErrorNetworkConnection();
    public void clearPassword();
}
