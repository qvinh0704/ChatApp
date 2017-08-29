package com.example.quangvinh.chatapprx.Presenter.Register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.EditText;

import com.example.quangvinh.chatapprx.Data.User;

/**
 * Created by QuangVinh on 3/15/2017.
 */

public interface PresenterImpRegister {
    public void validateButtonRegister(EditText edtEmail, EditText edtFullname, EditText edtPassword);
    public void signUp(User user);
    public void setFileAvatar(Bitmap bitmap);
    public void intentToBitmap(Intent intent);
    public void goToLogin();
}
