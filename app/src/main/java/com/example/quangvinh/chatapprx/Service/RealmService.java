package com.example.quangvinh.chatapprx.Service;

import android.content.Context;
import android.util.Log;

import com.example.quangvinh.chatapprx.Data.ChatDialog;
import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.Message;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Helper.LogUtil;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by QuangVinh on 4/21/2017.
 */

public class RealmService {
    Realm realm;
    private static RealmService realmService;

    private RealmService(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();

    }

    public static RealmService getRealmService(Context context) {
        if (realmService == null)
            realmService = new RealmService(context);
        return realmService;
    }

    //Tested
    public void addUser(User user) {
        try {

            realm.beginTransaction();
            user.setIdRealm(getNextId_USER());
            String name = user.getName();
            if (name != null)
                name = name.replaceAll("'", "''");
            user.setName(name);
            User realmUser = realm.copyToRealm(user);
            realm.commitTransaction();
        } catch (
                RealmException error) {
            LogUtil.error(error.getMessage());
        }

    }

    //Tested
    public void addMessage(MessageUI messageUI) {
        try {
            Message message = HelperTransformation.changeMessageUI_To_Message(messageUI);
            RealmResults realmResults = realm.where(Message.class).findAll();
            String body = message.getContent();
            body = body.replaceAll("'", "''");
            message.setContent(body);
            message.setIdRealm(getNextId_MESSAGE());
            realm.beginTransaction();
            Message realmMessage = realm.copyToRealmOrUpdate(message);
            realm.commitTransaction();
        } catch (RealmException error) {
            LogUtil.error(error.getMessage());
        }


    }

    //Tested
    public int getNextId_MESSAGE() {
        try {
            int id = realm.where(Message.class).max("idRealm").intValue();
            id++;
            return id;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    //Tested
    public int getNextId_USER() {
        try {
            int id = realm.where(User.class).max("idRealm").intValue();
            id++;
            return id;
        } catch (NullPointerException e) {
            LogUtil.error(e.getMessage());
            return 0;
        }
    }

    //Tested
    public int getNextId_CHATDIALOG() {
        try {
            int id = realm.where(ChatDialog.class).max("idRealm").intValue();
            return id + 1;
        } catch (NullPointerException e) {
            return 0;
        }
    }


    //Tested
    public boolean checkUserExistByEmail(String email) {
        try {
            User user = realm.where(User.class).equalTo("email", email).findFirst();
            if (user != null) {
                return true;
            }
            return false;
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return false;
    }

    //Tested
    public void addChatDialog(ChatDialogUI chatDialogUI) {
        try {
            ChatDialog chatDialog = HelperTransformation.changeChatDialogUI_To_ChatDialog(chatDialogUI);
            chatDialog.setIdRealm(getNextId_CHATDIALOG());

            if (checkUserExistByEmail(chatDialogUI.getReceiver().getEmail()) == false)
                addUser(chatDialogUI.getReceiver());
            if (checkUserExistByEmail(chatDialogUI.getSender().getEmail()) == false)
                addUser(chatDialogUI.getSender());
            realm.beginTransaction();
            ChatDialog copyChatDialog = realm.copyToRealm(chatDialog);
            realm.commitTransaction();
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
    }

    //Tested
    public User getUserByQBUserID(String id) {
        try {
            User user = realm.where(User.class).equalTo("qbuserID", id).findFirst();
            if (user != null) {
                User resultUser = new User(user.getQbuserID(), user.getName(),
                        user.getEmail(), user.getPassword(),
                        user.getLoginQuickblox(), user.getPassQuickblox(),
                        user.getSex(), user.getUrlImage());

                return resultUser;
            }
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    //Tested
    public ChatDialog getChatDialogById(String id) {
        try {
            ChatDialog chatDialog = realm.where(ChatDialog.class).equalTo("dialogID", id).findFirst();
            return chatDialog;
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    //Tested
    public ArrayList<ChatDialogUI> getListChatDialogBySenderID(String senderID) {
        try {
            realm.beginTransaction();
            RealmResults<ChatDialog> realmResults = realm.where(ChatDialog.class)
                    .equalTo("senderID", senderID)
                    .or()
                    .equalTo("receiverID", senderID)
                    .findAll();
            ArrayList<ChatDialog> results = (ArrayList<ChatDialog>) realm.copyFromRealm(realmResults);
            realm.commitTransaction();
            ArrayList<ChatDialogUI> chatDialogUIs = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                User sender = getUserByQBUserID(results.get(i).getSenderID());
                User receiver = getUserByQBUserID(realmResults.get(i).getReceiverID());
                ChatDialogUI chatDialogUI = HelperTransformation.changeChatDialog_To_ChatDialogUI(results.get(i), sender, receiver);
                chatDialogUIs.add(chatDialogUI);
            }
            return chatDialogUIs;
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return new ArrayList<ChatDialogUI>();
    }

    //Tested
    public boolean checkDiaglogExist(String idDialog) {
        try {
            ChatDialog chatDialog = realm.where(ChatDialog.class).equalTo("dialogID", idDialog).findFirst();
            if (chatDialog != null)
                return true;
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return false;
    }

    //Tested
    public void markRememberUserDatabase(String email) {
        try {
            realm.beginTransaction();
            User user = realm.where(User.class).equalTo("email", email).findFirst();
            if (user != null) {
                user.setLastestLogin(1);
            }
            realm.commitTransaction();
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
    }

    //Tested
    public int unmarkRememberUserDatabase(String email) {
        try {
            realm.beginTransaction();
            User user = realm.where(User.class).equalTo("email", email).findFirst();
            if (user != null) {
                user.setLastestLogin(0);
                realm.commitTransaction();
                return 1;
            }
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        realm.commitTransaction();
        return 0;
    }

    //Tested
    public User getUserLastLogin() {
        try {
            User user = realm.where(User.class).equalTo("lastestLogin", 1).findFirst();
            if (user != null) {
                User resultUser = new User(user.getQbuserID(), user.getName(),
                        user.getEmail(), user.getPassword(),
                        user.getLoginQuickblox(), user.getPassQuickblox(),
                        user.getSex(), user.getUrlImage());

                return resultUser;
            }
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    //Tested
    public void updateUser(User user) {
        try {
            realm.beginTransaction();
            String name = user.getName();
            name = name.replaceAll("'", "''");
            user.setName(name);
            User userSearch = realm.where(User.class).equalTo("email", user.getEmail()).findFirst();
            user.setIdRealm(userSearch.getIdRealm());
            user.setSex("Male");
            User userRealm = realm.copyToRealmOrUpdate(user);
            realm.commitTransaction();
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
    }

    //Tested
    public ArrayList<MessageUI> getListMessagesRealm(String dialogID) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            RealmResults<Message> realmResults = realm.where(Message.class).equalTo("dialogID", dialogID).findAll();
            ArrayList<Message> messages = (ArrayList<Message>) realm.copyFromRealm(realmResults);
            ArrayList<MessageUI> messageUIs = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                User sender = getUserByQBUserID(messages.get(i).getSenderID());
                User receiver = getUserByQBUserID(messages.get(i).getReceiverID());
                MessageUI messageUI = HelperTransformation.changeMessge_To_MessageUI(messages.get(i), sender, receiver);
                messageUIs.add(messageUI);
            }
            return messageUIs;
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return new ArrayList<MessageUI>();
    }

    //TEsted
    public ChatDialogUI getDialogByOccupants(String senderID, String receiverID) {
        try {
            ChatDialog chatDialog = realm.where(ChatDialog.class)
                    .equalTo("senderID", senderID)
                    .equalTo("receiverID", receiverID)
                    .or()
                    .equalTo("senderID", receiverID)
                    .equalTo("receiverID", senderID)
                    .findFirst();
            if (chatDialog != null) {
                User sender = getUserByQBUserID(chatDialog.getSenderID());
                User receiver = getUserByQBUserID(chatDialog.getReceiverID());
                ChatDialogUI chatDialogUI = HelperTransformation.changeChatDialog_To_ChatDialogUI(chatDialog, sender, receiver);
                return chatDialogUI;
            }
        } catch (RealmException e) {
            LogUtil.error(e.getMessage());
        }
        return null;
    }

    public void updateStatusMessage(MessageUI messageUI){
        try {
            Message message = HelperTransformation.changeMessageUI_To_Message(messageUI);
            Message realmMessage = realm.where(Message.class).equalTo("id", message.getId()).findFirst();
            message.setIdRealm(realmMessage.getIdRealm());
            realm.beginTransaction();
            Message resultMessage = realm.copyToRealmOrUpdate(message);
            realm.commitTransaction();
        }catch (RealmException e){
            LogUtil.error(e.getMessage());
        }
    }

    public ArrayList<MessageUI> getUnreadMessage(String mUserQBUserID){
        RealmResults<Message> realmResults = realm.where(Message.class).equalTo("status","")
                .beginGroup()
                .equalTo("receiverID",mUserQBUserID)
                .or()
                .equalTo("senderID",mUserQBUserID)
                .endGroup()
                .findAll().sort("idRealm");
        ArrayList<Message> messages = (ArrayList<Message>) realm.copyFromRealm(realmResults);
        ArrayList<MessageUI> messageUIs = new ArrayList<>();
        for(int i = messages.size() - 1; i >= 0 ;i--){
                User sender = getUserByQBUserID(messages.get(i).getSenderID());
                User receiver = getUserByQBUserID(messages.get(i).getReceiverID());
                MessageUI messageUI = HelperTransformation.changeMessge_To_MessageUI(messages.get(i), sender, receiver);
                messageUIs.add(messageUI);
        }
        LogUtil.debug("size unread "+messageUIs.size());
        return messageUIs;
    }


}
