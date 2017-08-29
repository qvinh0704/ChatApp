package com.example.quangvinh.chatapprx.Data;

import java.io.File;

/**
 * Created by QuangVinh on 3/21/2017.
 */

public class ModelUpdateUser {
    File avatar;
    Boolean isChangedAvatar = false;
    User user;

    public ModelUpdateUser(){
        user = new User();
    }

    public File getAvatar() {
        return avatar;
    }

    public Boolean getChangedAvatar() {
        return isChangedAvatar;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    public Boolean isChangedAvatar() {
        return isChangedAvatar;
    }

    public void setChangedAvatar(Boolean changedAvatar) {
        isChangedAvatar = changedAvatar;
    }
}
