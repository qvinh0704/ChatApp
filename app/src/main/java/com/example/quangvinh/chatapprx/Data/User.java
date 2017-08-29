package com.example.quangvinh.chatapprx.Data;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by QuangVinh on 2/24/2017.
 */

public class User extends RealmObject implements Serializable {
    @PrimaryKey
    private long idRealm;
    private String userID;
    private String qbuserID;
    private String name;
    private String email;
    private String password;
    private String loginQuickblox;
    private String passQuickblox;
    private String sex;
    private String urlImage="";
    private Place Location;
    private String status;
    private Integer lastestLogin;


    public User(String name, String email, String password, String loginQuickblox, String passQuickblox, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.loginQuickblox = loginQuickblox;
        this.passQuickblox = passQuickblox;
        this.sex = gender;
    }

    public User(String qbuserID, String name, String email, String password, String loginQuickblox, String passQuickblox, String sex, String urlImage) {
        this.qbuserID = qbuserID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.loginQuickblox = loginQuickblox;
        this.passQuickblox = passQuickblox;
        this.sex = sex;
        this.urlImage = urlImage;
    }

    public User(){
        lastestLogin = 0;
    }

    public String getLoginQuickblox() {
        return loginQuickblox;
    }

    public String getPassQuickblox() {
        return passQuickblox;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setLoginQuickblox(String loginQuickblox) {
        this.loginQuickblox = loginQuickblox;
    }

    public void setPassQuickblox(String passQuickblox) {
        this.passQuickblox = passQuickblox;
    }

    public Place getLocation() {
        return Location;
    }

    public void setLocation(Place location) {
        Location = location;
    }

    public String getQbuserID() {
        return qbuserID;
    }

    public void setQbuserID(String qbuserID) {
        this.qbuserID = qbuserID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getIdRealm() {
        return idRealm;
    }

    public void setIdRealm(long idRealm) {
        this.idRealm = idRealm;
    }

    public Integer getLastestLogin() {
        return lastestLogin;
    }

    public void setLastestLogin(Integer lastestLogin) {
        this.lastestLogin = lastestLogin;
    }
}
