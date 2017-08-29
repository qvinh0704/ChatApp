package com.example.quangvinh.chatapprx.Helper;

import android.location.LocationManager;

/**
 * Created by QuangVinh on 3/15/2017.
 */

public class Const {
    public static boolean REMEMBER_LOGIN = false;
    public static final int MIN_FULLNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 9;
    public static final String BUNDLE_USER_FROM_REGISTER = "userfromregister";
    public static final String ERROR_SESSION_QUICKBLOX = "SESSIONERROR_QUICKBLOX-";
    public static final String ERROR_SIGNUP_QUICKBLOX = "SIGNUP_QUICKBLOX-";
    public static final String ERROR_SIGNUP_FIREBASE = "SIGNUP_FIREBASE-";
    public static final String ERROR_SIGNIN_QUICKBLOX = "SIGNIN_QUICKBLOX-";
    public static final String ERROR_UPLOADAVATAR_QUICKBLOX = "UPLOAD_QUICKBLOX-";
    public static final String ERROR_ADDUSER_FIREBASE = "ADDUSER_FIREBASE-";
    public static final String ERROR_UPDATE_QUICKBLOX = "UPDATE_QUICKBLOX-";
    public static final String ERROR_SENDVERIFY_FIREBASE = "VERIFY_FIREBASE-";
    public static final String SQLITE_DATABASE_NAME = "ChatAppRx.sqlite";
    public static final String SQLITE_TABLE_USER = "USER";
    public static final String SQLITE_USER_MAIL_COLUMN = "email";
    public static final String SQLITE_USER_PASSWORD_COLUMN = "password";
    public static final String SQLITE_USER_LASTLOGIN_COLUMN = "lastlogin";
    public static final String SQLITE_USER_QBUSERID_COLUMN = "qbUserID";
    public static final String SQLITE_USER_URLAVATAR_COLUMN = "urlAvatar";
    public static final String SQLITE_USER_NAME_COLUMN = "name";
    public static final String SQLITE_USER_PASSQUICKBLOX_COLUMN = "PASSQUICKBLOX";
    public static final String SQLITE_USER_USERID_COLUMN = "USERIDCOLUMN";
    public static final String SQLITE_USER_SEX_COLUMN = "USERSEX";
    public static final Integer SQLITE_USER_EMAIL = 1;
    public static final Integer SQLITE_USER_PASSWORD = 2;
    public static final Integer SQLITE_USER_LASTLOGIN = 3;
    public static final Integer SQLITE_USER_IDQBUSER = 4;
    public static final Integer SQLITE_USER_URLAVATAR= 5;
    public static final Integer SQLITE_USER_NAME= 6;
    public static final Integer SQLITE_USER_PASSQUICKBLOX = 7;
    public static final Integer SQLITE_USER_USERID = 8;
    public static final Integer SQLITE_USER_SEX = 9;
    public static final String BUNDLE_USER_FROM_LOGIN ="userfromlogin";
    public static final String BUNDLE_USER_FROM_FINDUSER="userfromfinduser";
    public static final String BUNDLE_USER_FROM_UPDATE="userfromupdate";
    public static final String PROVIDER = LocationManager.NETWORK_PROVIDER;
    public static final String MESSAGE_STATUS_SEEN = "Seen";
    public static final String MESSAGE_STATUS_SENT = "Sent";
    public static final String MESSAGE_STATUS_DELIVERED="Delivered";
    public static final String ID_MESSAGE_TYPING = "messageistyping";
    public static final Integer LIMIT_MESSAGE = 10;
    public static final String BUNDLE_RECEIVER_FINDUSER = "BUNDLE_RECEIVER_FINDUSER";
}
