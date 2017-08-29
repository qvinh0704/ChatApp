package com.example.quangvinh.chatapprx.Presenter.UpdateUser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.User;

/**
 * Created by QuangVinh on 3/20/2017.
 */

public interface PresenterImpUpdateUser {
    public void updateUser(User newUser);
    public void validateButtonSave(EditText edtFullname, EditText edtPassword);
    public void getUserFromFindUser(Bundle bundle);
    public void loadProfile();
    public void showDiaglogVerify();
    public void requestUpdateUser(User newUser,String currPass);
    public void markAvatarChanged();
    public void changeIntentToBitmap(Intent intent);
    public void setFileAvatar(Bitmap bitmap);
    public void goToFindUser();
}
