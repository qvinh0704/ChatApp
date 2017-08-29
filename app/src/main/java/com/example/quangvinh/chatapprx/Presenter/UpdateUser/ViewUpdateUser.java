package com.example.quangvinh.chatapprx.Presenter.UpdateUser;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.quangvinh.chatapprx.Data.User;


/**
 * Created by QuangVinh on 3/20/2017.
 */

public interface ViewUpdateUser {
    public void enableButtonSave();
    public void disableButtonSave();
    public void showUpdateUserSuccess();
    public void showUpdateFailed(String message);
    public void enableInputField();
    public void disableInputField();
    public void showProfileUser(User user);
    public void showProgressbar();
    public void hideProgressbar();
    public void showGetProfileFailed();
    public void showDiaglogVerify();
    public void showVerifyUserFailed();
    public void showAvatarToImageview(Bitmap bitmap);
    public void closeDialog();
    public void goToFindUser(Bundle user);
}
