package com.example.quangvinh.chatapprx.Service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.quangvinh.chatapprx.Data.ChatDialog;
import com.example.quangvinh.chatapprx.Data.Message;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;

import java.util.ArrayList;

;

/**
 * Created by QuangVinh on 3/20/2017.
 */

public class SQLiteService extends SQLiteOpenHelper {
    public SQLiteService(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void executeQeryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    //Changed
    public User getUserLastLogin() throws SQLiteException {
        Cursor cursor = getData("SELECT * FROM " + Const.SQLITE_TABLE_USER);
        User user = null;
        while (cursor.moveToNext()) {
            int lastLogin = cursor.getInt(Const.SQLITE_USER_LASTLOGIN);
            LogUtil.debug(cursor.getString(Const.SQLITE_USER_NAME) + " " + lastLogin);
            if (lastLogin == 1) {
                user = new User();
                user.setEmail(cursor.getString(Const.SQLITE_USER_EMAIL));
                user.setPassword(cursor.getString(Const.SQLITE_USER_PASSWORD));
                user.setPassQuickblox(cursor.getString(Const.SQLITE_USER_PASSQUICKBLOX));
                user.setName(cursor.getString(Const.SQLITE_USER_NAME));
                user.setUrlImage(cursor.getString(Const.SQLITE_USER_URLAVATAR));
                user.setUserID(cursor.getString(Const.SQLITE_USER_USERID));
                user.setQbuserID(cursor.getString(Const.SQLITE_USER_IDQBUSER));
                user.setSex(cursor.getString(Const.SQLITE_USER_SEX));
                break;
            }
        }
        return user;
    }



    //Changed
    public void markRememberUserDatabase(String email) throws SQLiteException {
        executeQeryData("UPDATE " + Const.SQLITE_TABLE_USER + " SET " + Const.SQLITE_USER_LASTLOGIN_COLUMN + " = 1" + " WHERE " +
                Const.SQLITE_USER_MAIL_COLUMN + " = '" + email + "'");
    }

    //Changed
    public int unmarkRememberUserDatabase(String email) {
        try {
            executeQeryData("UPDATE " + Const.SQLITE_TABLE_USER + " SET " + Const.SQLITE_USER_LASTLOGIN_COLUMN + " = 0" + " WHERE " +
                    Const.SQLITE_USER_MAIL_COLUMN + " = '" + email + "'");

            LogUtil.debug("unmark Rememeber");

            Cursor cursor = getData("SELECT * FROM " + Const.SQLITE_TABLE_USER);
            while (cursor.moveToNext()) {
                int lastLogin = cursor.getInt(Const.SQLITE_USER_LASTLOGIN);
            }

        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }

        return 1;
    }

    //Changed
    public boolean checkUserExistByEmail(String email) throws SQLiteException {
        Cursor cursor = getData("select * from " + Const.SQLITE_TABLE_USER + " where " + Const.SQLITE_USER_MAIL_COLUMN + " = '" + email + "'");
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    //Changed
    public void addUser(User user)  {
        try{
            String name = user.getName();
            name = name.replaceAll("'","''");
            executeQeryData("insert into " + Const.SQLITE_TABLE_USER + " values (null,'" + user.getEmail() + "','"
                    + user.getPassword() + "',1,'" + user.getQbuserID() + "', '"+ user.getUrlImage()+"','"+name+"', '"+
                    user.getPassQuickblox()+"', '"+
                    user.getUserID()+"', '"+
                    user.getSex() + "')");

        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
    }

    //Changed
    public void createTableUser() throws SQLiteException {
        try{
            executeQeryData("CREATE TABLE IF NOT EXISTS " + Const.SQLITE_TABLE_USER + "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Const.SQLITE_USER_MAIL_COLUMN + " VARCHAR, " + Const.SQLITE_USER_PASSWORD_COLUMN + " VARCHAR," +
                    Const.SQLITE_USER_LASTLOGIN_COLUMN + " INTEGER, "+Const.SQLITE_USER_QBUSERID_COLUMN+" VARCHAR,"
                    +Const.SQLITE_USER_URLAVATAR_COLUMN+ " VARCHAR, "+Const.SQLITE_USER_NAME_COLUMN+"VARCHAR, "+
                    Const.SQLITE_USER_PASSQUICKBLOX_COLUMN+" VARCHAR, "+
                    Const.SQLITE_USER_USERID_COLUMN+" VARCHAR, "+
                    Const.SQLITE_USER_SEX_COLUMN+" VARCHAR)");
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }

    }


    //Changed
    public void updateUser(User user){
        try {
            String name = user.getName();
            name = name.replaceAll("'","''");
            executeQeryData("UPDATE " + Const.SQLITE_TABLE_USER + " SET " + Const.SQLITE_USER_PASSWORD_COLUMN + " = '" + user.getPassword() +"',"+
                    Const.SQLITE_USER_NAME_COLUMN + " = '"+name+"', "+
                    Const.SQLITE_USER_URLAVATAR_COLUMN + " = '" + user.getUrlImage() + "'"+
                    Const.SQLITE_USER_SEX_COLUMN + " = '"+user.getSex()+"' "+
                    " WHERE " +Const.SQLITE_USER_MAIL_COLUMN + " = '" + user.getEmail() + "'");
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
    }

    //Changed
//    public void addMessage(Message message) {
//        try {
//            String body = message.getContent();
//            body = body.replaceAll("'","''");
//            String sql = "insert into MESSAGE values ('" + message.getId() + "', '" + message.getDialogID() + "', '"
//                    + body + "','" + message.getStatus() + "'," + message.getRecipientID() + ",null)";
//            executeQeryData(sql);
//            LogUtil.debug("addMessage SQLite");
//
//        } catch (SQLiteException e) {
//            LogUtil.error(e.getMessage());
//        }
//    }

    //Changed
    public void createTableMessage() throws SQLiteException {
        try{
            executeQeryData("CREATE TABLE IF NOT EXISTS MESSAGE(id VARCHAR,dialogID VARCHAR, content VARCHAR, "
                    + "status VARCHAR, recipientID INTEGER, idMessage INTEGER PRIMARY KEY AUTOINCREMENT)");
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }

    }

    //Changed
    public ArrayList<Message> getListMessagesSQLite(String dialogID) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            String sql = "Select * from MESSAGE where dialogID = '" + dialogID + "'";
            Cursor cursor = getData(sql);
            LogUtil.debug("Cursor size: " + cursor.getCount());
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getString(0));
                message.setDialogID(cursor.getString(1));
                message.setContent(cursor.getString(2));
                message.setStatus(cursor.getString(3));
//                message.setRecipientID(cursor.getInt(4));
                result.add(message);
            }
        } catch (SQLiteException e) {
            LogUtil.error(e.getMessage());
        }
        return result;
    }

    //Changed
    public void createTableDialog() throws SQLiteException {
        try{
            executeQeryData("CREATE TABLE IF NOT EXISTS DIALOG (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "dialogID VARCHAR, idSender VARCHAR, idReceiver VARCHAR)");
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
    }

    //Changed
    public boolean checkDiaglogExist(String idDialog) {
        try{
            Cursor cursor = getData("select * from DIALOG where dialogID = '" + idDialog + "'");
            if (cursor.getCount() > 0) {
                return true;
            }
            return false;
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
        return false;
    }
    //Changed
    public User getUserByID(String id) throws SQLiteException {
        User user = new User();
        try {
            Cursor cursor = getData("SELECT * FROM " + Const.SQLITE_TABLE_USER + " where qbUserID = '" + id + "'");
            user.setQbuserID(id);
            if (cursor.moveToNext()) {
                String email = cursor.getString(Const.SQLITE_USER_EMAIL);
                user.setEmail(email);
                String password = cursor.getString(Const.SQLITE_USER_PASSWORD);
                user.setPassword(password);
                String name = cursor.getString(Const.SQLITE_USER_NAME);
                user.setName(name);
                String url = cursor.getString(Const.SQLITE_USER_URLAVATAR);
                user.setUrlImage(url);
            }else{
                user = null;
            }
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
        return  user;
    }

    //Changed
    public ChatDialog getDialogByOccupants(String idSender, String idReceiver){
        try{
            Cursor cursor = getData("select * from DIALOG where (idSender = '"+idSender+"' and idReceiver = '"+idReceiver+"')" +
                    "or (idSender = '"+idReceiver+"' and idReceiver = '"+idSender+"')");
            ChatDialog chatDialog = null;
           //Test
            ArrayList<User> listUser = new ArrayList<>();
            Cursor cursorUser = getData("select * from "+Const.SQLITE_TABLE_USER);
            while(cursorUser.moveToNext()){
                User user = new User();
                String email = cursorUser.getString(Const.SQLITE_USER_EMAIL);
                user.setEmail(email);
                String pass = cursorUser.getString(Const.SQLITE_USER_PASSWORD);
                user.setPassword(pass);
                String name = cursorUser.getString(Const.SQLITE_USER_NAME);
                user.setName(name);
                String url = cursorUser.getString(Const.SQLITE_USER_URLAVATAR);
                user.setUrlImage(url);
                listUser.add(user);
            }
            LogUtil.debug(listUser.size() + "");
            //-----------------------------
            if(cursor.moveToNext()){
                chatDialog = new ChatDialog();
                chatDialog.setDialogID(cursor.getString(1));
                User sender = getUserByID(idSender);
                User receiver = getUserByID(idReceiver);
//                chatDialog.setSender(sender);
//                chatDialog.setReceiver(receiver);
                return chatDialog;
            }
            return chatDialog;
        }catch (SQLiteException e){
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    //Changed
//    public void addChatDialog(ChatDialog chatDialog){
//        try{
//            executeQeryData("INSERT INTO DIALOG VALUES(NULL, '"+chatDialog.getDialogID()+"', '"+chatDialog.getSender().getQbuserID()+"','"+chatDialog.getReceiver().getQbuserID()+"') ");
//            if(checkUserExistByEmail(chatDialog.getReceiver().getEmail()) == false)
//                addUser(chatDialog.getReceiver());
//            if(checkUserExistByEmail(chatDialog.getSender().getEmail()) == false)
//                addUser(chatDialog.getSender());
//        }catch (SQLiteException e){
//            LogUtil.error(e.getMessage());
//        }
//    }

    //Changed
    public ChatDialog getChatDialogById(String dialogID){
        Cursor cursor = getData("SELECT * FROM DIALOG WHERE dialogID = '"+dialogID+"'");
        ChatDialog tempChatDialog = new ChatDialog();
        while(cursor.moveToNext()){
            tempChatDialog.setDialogID(cursor.getString(1));
            User sender = getUserByID(cursor.getString(2));
//            tempChatDialog.setSender(sender);
//            User receiver = getUserByID(cursor.getString(3));
//            tempChatDialog.setReceiver(receiver);
        }
        return tempChatDialog;
    }

    //Changed
    public ArrayList<ChatDialog> getListChatDialogBySenderID(String senderID){
        ArrayList<ChatDialog> dialogArrayList = new ArrayList<>();
        Cursor cursor = getData("SELECT * FROM DIALOG WHERE idSender = '"+senderID+"' or idReceiver = '"+senderID+"'");
        while(cursor.moveToNext()){
            ChatDialog tempChatDialog = new ChatDialog();
            tempChatDialog.setDialogID(cursor.getString(1));
            User sender = getUserByID(cursor.getString(2));
//            tempChatDialog.setSender(sender);
//            User receiver = getUserByID(cursor.getString(3));
//            tempChatDialog.setReceiver(receiver);
            dialogArrayList.add(tempChatDialog);
        }
        return dialogArrayList;
    }
}
