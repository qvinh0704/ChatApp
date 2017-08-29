package com.example.quangvinh.chatapprx.Helper;

import java.io.InputStream;

/**
 * Created by QuangVinh on 3/9/2017.
 */

public interface IGetAvatar {
    public void onSuccess(InputStream inputStream);
    public void onError(String message);
}
