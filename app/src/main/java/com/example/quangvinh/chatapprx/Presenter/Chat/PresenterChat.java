package com.example.quangvinh.chatapprx.Presenter.Chat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.ModelChat;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Service.ListenMessageService;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.example.quangvinh.chatapprx.Service.RealmService;
import com.example.quangvinh.chatapprx.Service.SQLiteService;
import com.example.quangvinh.chatapprx.Service.WifiReceiver;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by QuangVinh on 3/27/2017.
 */

public class PresenterChat implements PresenterImpChat {
    ModelChat modelChat;
    QuickbloxService mQuickbloxService;
    ViewChat mViewChat;
    Activity mActivity;
    SQLiteService sqLiteService;
    EditText edtChat;
    RealmService realmService;
    boolean availableLoadMore = true;


    public PresenterChat(Activity activity, ViewChat viewChat) {
        mActivity = activity;
        mViewChat = viewChat;
        mQuickbloxService = QuickbloxService.getQuickblox_Instance(activity);
        modelChat = new ModelChat();
        sqLiteService = new SQLiteService(activity, Const.SQLITE_DATABASE_NAME, null, 1);
        realmService = RealmService.getRealmService(activity.getApplicationContext());
    }


    @Override
    public void initPresenterChat(Bundle bundle, EditText editTexttchat) {
        mViewChat.disableTypeMessage();
        if (ListenMessageService.bus != null) {
            ListenMessageService.bus.register(this);
        }
        if(WifiReceiver.bus != null){
            WifiReceiver.bus.register(this);
        }
        edtChat = editTexttchat;
        getBundleFromFindUser(bundle);
        sqLiteService.createTableDialog();
        sqLiteService.createTableMessage();
        //getMessage SQLite
        getMessagesRealm();
        loginChat();
    }

    @Override
    public void loginChat() {
        mQuickbloxService.loginToChat()
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.error(e.getMessage());
                        mViewChat.showError();
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        getDialog(modelChat.getmUser().getQbuserID(), modelChat.getReceiver().getQbuserID());
                    }
                });
    }

    @Override
    public void getDialog(String idSender, String idReceiver) {
        mQuickbloxService.getDialogs().subscribe(new Subscriber<ArrayList<QBChatDialog>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.error(e.getMessage());
                mViewChat.showMessage(e.getMessage());
                mViewChat.showError();
            }

            @Override
            public void onNext(ArrayList<QBChatDialog> qbChatDialogs) {
                boolean hasDialog = false;
                for (int i = 0; i < qbChatDialogs.size(); i++) {
                    String occupant1 = qbChatDialogs.get(i).getOccupants().get(0).toString();
                    String occupant2 = qbChatDialogs.get(i).getOccupants().get(1).toString();
                    if (occupant1.equals(idSender) && occupant2.equals(idReceiver)) {
                        setDialog(qbChatDialogs.get(i));
                        hasDialog = true;
                        break;
                    } else if (occupant2.equals(idSender) && occupant2.equals(idReceiver)) {
                        setDialog(qbChatDialogs.get(i));
                        hasDialog = true;
                        break;
                    }
                }
                mViewChat.enableTypeMessage();
                mViewChat.hideError();
                if (hasDialog == false) {
                    createDialog();
                }
            }
        });
    }

    @Override
    public void createDialog() {
        mQuickbloxService.createDialogPrivate(Integer.valueOf(modelChat.getReceiver().getQbuserID())).subscribe(new Subscriber<QBChatDialog>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mViewChat.showMessage(e.getMessage());
                LogUtil.error(e.getMessage());
                mViewChat.showError();
            }

            @Override
            public void onNext(QBChatDialog qbChatDialog) {
                setDialog(qbChatDialog);
                mViewChat.hideError();
            }
        });
    }

    @Override
    public void getMessagesRealm() {
        String idSender = modelChat.getmUser().getQbuserID().toString();
        String idReceiver = modelChat.getReceiver().getQbuserID().toString();
//        ChatDialogUI chatDialog = sqLiteService.getDialogByOccupants(idSender, idReceiver);
        ChatDialogUI chatDialog = realmService.getDialogByOccupants(idSender, idReceiver);
        if (chatDialog != null) {
//            ArrayList<MessageUI> listMessage = sqLiteService.getListMessagesSQLite(chatDialog.getDialogID());
            ArrayList<MessageUI> listMessage = realmService.getListMessagesRealm(chatDialog.getDialogID());
            modelChat.setListMessage(listMessage);

        } else {
            modelChat.setListMessage(new ArrayList<MessageUI>());
        }
        mViewChat.refreshRecyclerView();
    }

    @Override
    public void catchEventEditTextChat(EditText editText) {
        RxTextView.textChanges(editText)
                .subscribe(new Subscriber<CharSequence>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (charSequence.length() == 0) {
                            mQuickbloxService.sendTypingStatus(modelChat.getmQBChatDialog(), false);
                        } else {
                            mQuickbloxService.sendTypingStatus(modelChat.getmQBChatDialog(), true);
                        }
                    }
                });

    }

    @Override
    public void getMessagesQuickblox(int pageSkip) {
        mQuickbloxService.getMessages(modelChat.getmQBChatDialog(), pageSkip).subscribe(new Subscriber<ArrayList<QBChatMessage>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mViewChat.showMessage(e.getMessage());
                LogUtil.error(e.getMessage());
            }

            @Override
            public void onNext(ArrayList<QBChatMessage> qbChatMessages) {

                ChatDialogUI chatDialog = new ChatDialogUI();
                String idDialog = modelChat.getmQBChatDialog().getDialogId().toString();
//                    if (sqLiteService.checkDiaglogExist(idDialog) == false) {
                if (realmService.checkDiaglogExist(idDialog) == false) {
                    chatDialog.setDialogID(modelChat.getmQBChatDialog().getDialogId());
                    chatDialog.setReceiver(modelChat.getReceiver());
                    chatDialog.setSender(modelChat.getmUser());
//                        sqLiteService.addChatDialog(chatDialog);
                    realmService.addChatDialog(chatDialog);
                }
//                    If sqlite have no message, add Messages from Quickblox to sqlite
                //If size != 0, get more message so don't need to save message
                if (modelChat.getListMessage().size() == 0) {
                    ArrayList<MessageUI> listMessage = new ArrayList<MessageUI>();
                    int size = qbChatMessages.size() - 1;

                    for (int i = size; i >= 0; i--) {
                        MessageUI message = HelperTransformation.changeQBChatMessageToMessageUI(qbChatMessages.get(i));
//                            sqLiteService.addMessage(message);
                        realmService.addMessage(message);
                        listMessage.add(message);
                    }

                    //Add message to local list
                    modelChat.setListMessage(listMessage);
                    mViewChat.refreshRecyclerView();
                } else {
                    int n = qbChatMessages.size();
                    for (int i = 0; i < n; i++) {
                        MessageUI tempMessage = HelperTransformation.changeQBChatMessageToMessageUI(qbChatMessages.get(i));
                        modelChat.getListMessage().add(0, tempMessage);
                    }
                    if (qbChatMessages.size() == 0) {
                        //Load full message
                        availableLoadMore = false;
                    } else {
                        //mark load more success
                        availableLoadMore = true;
                    }
                    mViewChat.loadMoreMessage(Const.LIMIT_MESSAGE - 1);
                }

                //mViewChat.showMessage("getMessages");

                mViewChat.hideProgressbar();


            }
        });
    }

    @Override
    public void sendMessage(String content) {
        Gson gson = new Gson();
        MessageUI message = new MessageUI();
        message.setContent(content);
        message.setDialogID(modelChat.getmQBChatDialog().getDialogId());
        message.setReceiver(modelChat.getReceiver());
        message.setSender(modelChat.getmUser());
        content = gson.toJson(message);
        mQuickbloxService.sendMessage(content, modelChat.getmQBChatDialog()).subscribe(new Subscriber<QBChatMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.error(e.getMessage());
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                mViewChat.showError();
            }

            @Override
            public void onNext(QBChatMessage qbChatMessage) {
                mViewChat.hideError();
                qbChatMessage.setRecipientId(Integer.valueOf(modelChat.getReceiver().getQbuserID()));
                MessageUI message = HelperTransformation.changeQBChatMessageToMessageUI(qbChatMessage);
                message.setStatus(Const.MESSAGE_STATUS_SENT);
                //Add message to SQlite
                modelChat.getListMessage().add(message);
//                sqLiteService.addMessage(message);
                realmService.addMessage(message);
                realmService.updateStatusMessage(message);
                mViewChat.refreshRecyclerView();
            }
        });
    }

    @Override
    public void getBundleFromFindUser(Bundle bundle) {
        if (bundle != null) {
            User user = (User) bundle.get(Const.BUNDLE_USER_FROM_FINDUSER);
            User receiver = (User) bundle.get(Const.BUNDLE_RECEIVER_FINDUSER);
            String mUserId = mQuickbloxService.getQbUser().getId().toString();
            if (user != null && receiver.getQbuserID() != null) {
                if (user.getQbuserID().equals(mUserId)) {
                    modelChat.setmUser(user);
                    modelChat.setReceiver(receiver);
                } else {
                    modelChat.setmUser(receiver);
                    modelChat.setReceiver(user);
                }
                mViewChat.showNameInToolbar(modelChat.getReceiver().getName());
            }
        }
    }

    @Override
    public ArrayList<MessageUI> getListMessageFromModel() {
        return modelChat.getListMessage();
    }

    @Override
    public Integer getRecipientID() {
        return Integer.valueOf(modelChat.getReceiver().getQbuserID());
    }

    public void initMessageStatusListener() {
        mQuickbloxService.initMessageStatusListener(modelChat.getmQBChatDialog()).subscribe(new Subscriber<MessageUI>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.error(e.getMessage());
            }

            @Override
            public void onNext(MessageUI temp) {
                if (temp.getStatus().equals(Const.MESSAGE_STATUS_DELIVERED)) {
                    MessageUI message = modelChat.getMessageByID(temp.getId());
                    message.setStatus(Const.MESSAGE_STATUS_DELIVERED);
                    realmService.updateStatusMessage(message);
                } else if (temp.getStatus().equals(Const.MESSAGE_STATUS_SEEN)) {
                    MessageUI message = modelChat.getMessageByID(temp.getId());
                    message.setStatus(Const.MESSAGE_STATUS_SEEN);
                    realmService.updateStatusMessage(message);
                }
                mViewChat.refreshRecyclerView();
            }
        });
    }

    @Override
    public void initTypingStatus() {
        mQuickbloxService.initTypingStatus(modelChat.getmQBChatDialog())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            showIsTyping();
                        } else {
                            hideIsTyping();
                        }
                    }
                });
    }

    @Override
    public void showIsTyping() {
        if (!modelChat.isTyping()) {
            MessageUI message = new MessageUI();
            message.setId(Const.ID_MESSAGE_TYPING);
            message.setContent("");
            message.getReceiver().setQbuserID(-1 + "");
            modelChat.getListMessage().add(message);
            mViewChat.refreshRecyclerView();
            modelChat.setTyping(true);
        }
    }

    @Override
    public void hideIsTyping() {
        modelChat.setTyping(false);
        modelChat.removeTyping_Message();
        mViewChat.refreshRecyclerView();
    }

    @Override
    public void loadMoreMessages() {
        if (availableLoadMore == true) {
            mViewChat.showProgressbar();
            getMessagesQuickblox(modelChat.getListMessage().size());
            LogUtil.debug("loadMore Message");
            availableLoadMore = false;
        }
    }

    @Subscribe
    public void getMessage(MessageUI message) {
        modelChat.addMessage(message);
        mViewChat.refreshRecyclerView();
    }

    @Subscribe
    public void getMessage(String stateWifi){
        if(stateWifi.equals("YES")){
            loginChat();
        }else{
            mViewChat.showError();
        }
    }

    @Override
    public void markReadMessage() {
        if (modelChat.getmQBChatDialog() != null) {
            String dialogId = modelChat.getmQBChatDialog().getDialogId();
            if (mQuickbloxService.getLastestMessages().containsKey(dialogId)) {
                try {
                    modelChat.getmQBChatDialog().readMessage(mQuickbloxService.getLastestMessages().get(dialogId));
                    MessageUI messageUI = HelperTransformation.changeQBChatMessageToMessageUI(mQuickbloxService.getLastestMessages().get(dialogId));
                    messageUI.setStatus(Const.MESSAGE_STATUS_SEEN);
                    //Update status all messages by dialog id
                    ArrayList<MessageUI> listUnreadMessage = realmService.getUnreadMessage(modelChat.getmUser().getQbuserID());
                    for (int i = 0; i < listUnreadMessage.size(); i++) {
                        String dialogID = listUnreadMessage.get(i).getDialogID();
                        if (dialogID.equals(messageUI.getDialogID())) {
                            listUnreadMessage.get(i).setStatus(Const.MESSAGE_STATUS_SEEN);
                            realmService.updateStatusMessage(listUnreadMessage.get(i));
                        }
                    }
                    mQuickbloxService.getLastestMessages().remove(dialogId);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setDialog(QBChatDialog qbChatDialog) {
        modelChat.setmQBChatDialog(qbChatDialog);
        mViewChat.enableTypeMessage();
        if (modelChat.getListMessage().size() == 0) {
            getMessagesQuickblox(0);
        }else{
            getMessagesQuickblox(modelChat.getListMessage().size());
        }
        initMessageStatusListener();
        initTypingStatus();
        catchEventEditTextChat(edtChat);
    }


}
