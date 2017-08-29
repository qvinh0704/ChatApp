package com.example.quangvinh.chatapprx.Service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.google.android.gms.maps.model.LatLng;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by QuangVinh on 3/6/2017.
 */

public class QuickbloxService {
    static final String APP_ID = "55468";
    static final String AUTH_KEY = "hrQOxY6TPd-vs8n";
    static final String AUTH_SECRET = "OkZrcK6JbCG9UKb";
    static final String ACCOUNT_KEY = "q1SVk8KUi3xVzKHhx6ta";
    Performer<QBSession> performerCreateSession;
    Performer<QBUser> performerSignupUser;
    QBUser instanceUser;
    QBChatService chatService;
    QBLocation mQBLocation;
    HashMap<String, QBChatMessage> listLastestMessages = new HashMap<String, QBChatMessage>();
    Context context;
    private static QuickbloxService mQuickbloxService;


    private QuickbloxService(Context mContext) {
        QBSettings.getInstance().init(mContext, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
        performerCreateSession = QBAuth.createSession();
        QBChatService.setDebugEnabled(false); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(10000);

        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
//        chatServiceConfigurationBuilder.setSocketTimeout(10); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);
        chatService = QBChatService.getInstance();
        chatService.setUseStreamManagement(true);
        context = mContext;
    }

    public static QuickbloxService getQuickblox_Instance(Context mContext) {
        if (mQuickbloxService == null) {
            mQuickbloxService = new QuickbloxService(mContext);
        }
        return mQuickbloxService;
    }

    public Observable<Boolean> createSessionQuickblox() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                performerCreateSession.performAsync(new QBEntityCallback<QBSession>() {
                    @Override
                    public void onSuccess(QBSession qbSession, Bundle bundle) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(Const.ERROR_SESSION_QUICKBLOX + e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<QBUser> requestSignupQuickblox(User user) {
        return Observable.create(new Observable.OnSubscribe<QBUser>() {
            @Override
            public void call(Subscriber<? super QBUser> subscriber) {
                QBUser qbUser = new QBUser(user.getEmail(), user.getPassword());
                performerSignupUser = QBUsers.signUp(qbUser);
                performerSignupUser.performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        instanceUser = qbUser;
                        user.setQbuserID(qbUser.getId().toString());
                        subscriber.onNext(qbUser);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(Const.ERROR_SIGNUP_QUICKBLOX + e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<QBUser> signinQuickblox(User user) {

        return Observable.create(new Observable.OnSubscribe<QBUser>() {
            @Override
            public void call(Subscriber<? super QBUser> subscriber) {
                if (chatService.isLoggedIn()) {
                    subscriber.onNext(chatService.getUser());
                    LogUtil.error("Account is logged");
                } else {
                    QBUser qbUser = new QBUser(user.getLoginQuickblox(), user.getPassQuickblox());
                    QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            instanceUser = qbUser;
                            instanceUser.setPassword(user.getPassQuickblox());
                            subscriber.onNext(qbUser);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            subscriber.onError(new Throwable(e.getMessage()));
                            LogUtil.error(Const.ERROR_SIGNIN_QUICKBLOX + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public Observable<Boolean> deleteUser_Quickblox() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBUsers.deleteUser(instanceUser.getId()).performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<Boolean> loginToChat() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                if (chatService.isLoggedIn()) {
                    subscriber.onNext(true);
                } else {
                    QBChatService.getInstance().login(instanceUser, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            subscriber.onNext(true);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            if (!e.getMessage().equals("You have already logged in chat")) {
                                subscriber.onError(new Throwable(e.getMessage()));
                                LogUtil.error("Login to chat-- " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public Observable<QBFile> upLoadAvatar(File file) {
        return Observable.create(new Observable.OnSubscribe<QBFile>() {
            @Override
            public void call(Subscriber<? super QBFile> subscriber) {
                QBContent.uploadFileTask(file, true, null, new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(int i) {

                    }
                }).performAsync(new QBEntityCallback<QBFile>() {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        subscriber.onNext(qbFile);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error(Const.ERROR_UPLOADAVATAR_QUICKBLOX + e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<Boolean> updateUserProfile() {
        instanceUser.setPassword(null);
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBUsers.updateUser(instanceUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error(Const.ERROR_UPDATE_QUICKBLOX + e.getMessage());
                    }
                });
            }
        });
    }


    public void setQbUser(QBUser temp) {
        instanceUser = temp;
    }

    public QBUser getQbUser() {
        return instanceUser;
    }

    public Observable<Boolean> createLocation(final LatLng mLocation, String status) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBLocation location = new QBLocation(mLocation.latitude, mLocation.longitude, status);
                QBLocations.createLocation(location).performAsync(new QBEntityCallback<QBLocation>() {
                    @Override
                    public void onSuccess(QBLocation qbLocation, Bundle bundle) {
                        mQBLocation = qbLocation;
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<ArrayList<QBLocation>> retrieveLocation(LatLng myLocation, float radius) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<QBLocation>>() {
            @Override
            public void call(Subscriber<? super ArrayList<QBLocation>> subscriber) {
                QBLocationRequestBuilder qbLocationRequestBuilder = new QBLocationRequestBuilder();
                qbLocationRequestBuilder.setPerPage(100);
                qbLocationRequestBuilder.setLastOnly();
                qbLocationRequestBuilder.setRadius(myLocation.latitude, myLocation.longitude, radius);
                QBLocations.getLocations(qbLocationRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBLocation>>() {
                    @Override
                    public void onSuccess(ArrayList<QBLocation> qbLocations, Bundle bundle) {
                        subscriber.onNext(qbLocations);

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<Boolean> updateLocation(LatLng newLocation, String status) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBLocation location = new QBLocation(newLocation.latitude, newLocation.longitude, status);
                location.setId(mQBLocation.getId());
                QBLocations.updateLocation(location).performAsync(new QBEntityCallback<QBLocation>() {
                    @Override
                    public void onSuccess(QBLocation qbLocation, Bundle bundle) {
                        subscriber.onNext(true);
                        LogUtil.debug(qbLocation.getStatus());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }


    public Observable<QBChatDialog> createDialogPrivate(int userID) {
        return Observable.create(new Observable.OnSubscribe<QBChatDialog>() {
            @Override
            public void call(Subscriber<? super QBChatDialog> subscriber) {
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(userID);
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        qbChatDialog.initForChat(QBChatService.getInstance());
                        LogUtil.debug(qbChatDialog.getOccupants().get(0) + "");
                        LogUtil.debug(qbChatDialog.getOccupants().get(1) + "");
                        subscriber.onNext(qbChatDialog);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        Log.e("AAA", "Create dialog error-- " + e.getMessage());

                    }
                });
            }
        });
    }

    public Observable<ArrayList<QBChatMessage>> getMessages(QBChatDialog qbChatDialog, int pageSkip) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<QBChatMessage>>() {
            @Override
            public void call(Subscriber<? super ArrayList<QBChatMessage>> subscriber) {
                if (qbChatDialog != null) {
                    QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
                    requestGetBuilder.setLimit(Const.LIMIT_MESSAGE);
                    requestGetBuilder.setPagesSkip(pageSkip);
                    requestGetBuilder.sortDesc("created_at");
                    QBRestChatService.getDialogMessages(qbChatDialog, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                        @Override
                        public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                            //Mark message read
                            subscriber.onNext(qbChatMessages);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            subscriber.onError(new Throwable(e.getMessage()));
                            LogUtil.error("getMessages Error-- " + e.getMessage());
                        }
                    });
                } else {
                    subscriber.onNext(new ArrayList<QBChatMessage>());
                }
            }
        });

    }

    public Observable<QBChatMessage> sendMessage(String content, QBChatDialog qbChatDialog) {
        return Observable.create(new Observable.OnSubscribe<QBChatMessage>() {
            @Override
            public void call(Subscriber<? super QBChatMessage> subscriber) {
                final QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setSaveToHistory(true);
                qbChatMessage.setBody(content);
                qbChatMessage.setMarkable(true);
                qbChatDialog.sendMessage(qbChatMessage, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        qbChatMessage.setDialogId(qbChatDialog.getDialogId());
                        subscriber.onNext(qbChatMessage);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error("sendMessage Error-- " + e.getMessage());
                    }
                });
            }
        });
    }


    public Observable<Boolean> signOutQuickBlox() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error(e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<Boolean> logOutChatService() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                chatService.logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        subscriber.onError(new Throwable(e.getMessage()));
                        LogUtil.error(e.getMessage());
                    }
                });
            }
        });
    }

    public Observable<MessageUI> initMessageStatusListener(QBChatDialog qbChatDialog) {
        return Observable.create(new Observable.OnSubscribe<MessageUI>() {
            @Override
            public void call(Subscriber<? super MessageUI> subscriber) {
                QBMessageStatusesManager messageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
                QBMessageStatusListener messageStatusListener = new QBMessageStatusListener() {
                    @Override
                    public void processMessageDelivered(String s, String s1, Integer integer) {
                        MessageUI message = new MessageUI();
                        message.setId(s);
                        message.setStatus(Const.MESSAGE_STATUS_DELIVERED);
                        subscriber.onNext(message);
                    }

                    @Override
                    public void processMessageRead(String s, String s1, Integer integer) {
                        MessageUI message = new MessageUI();
                        message.setId(s);
                        message.setStatus(Const.MESSAGE_STATUS_SEEN);
                        subscriber.onNext(message);
                    }
                };
                messageStatusesManager.addMessageStatusListener(messageStatusListener);
            }
        });
    }


    public Observable<Boolean> initTypingStatus(QBChatDialog qbChatDialog) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                QBChatDialogTypingListener qbChatDialogTypingListener = new QBChatDialogTypingListener() {
                    @Override
                    public void processUserIsTyping(String s, Integer integer) {
                        subscriber.onNext(true);
                    }

                    @Override
                    public void processUserStopTyping(String s, Integer integer) {
                        subscriber.onNext(false);
                    }
                };
                qbChatDialog.addIsTypingListener(qbChatDialogTypingListener);
            }
        });
    }

    public void sendTypingStatus(QBChatDialog qbChatDialog, boolean isTyping) {
        if (isTyping) {
            try {
                qbChatDialog.sendIsTypingNotification();
            } catch (XMPPException e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage());
            }
        } else {
            try {
                qbChatDialog.sendStopTypingNotification();
            } catch (XMPPException e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage());
            }
        }
    }

    public Observable<ArrayList<QBChatDialog>> getDialogs() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<QBChatDialog>>() {
            @Override
            public void call(Subscriber<? super ArrayList<QBChatDialog>> subscriber) {
                QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
                requestGetBuilder.setLimit(100);
                QBRestChatService.getChatDialogs(QBDialogType.PRIVATE, requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
//                        for(QBChatDialog temp : qbChatDialogs){
//                            String lastMessage = temp.getLastMessage();
//                            if(lastMessage == null){
//                                qbChatDialogs.remove(temp);
//                            }
//                        }
                        ArrayList<Integer> listRemove = new ArrayList<Integer>();
                        for (int i = 0; i < qbChatDialogs.size(); i++) {
                            String lastMessage = qbChatDialogs.get(i).getLastMessage();
                            if (lastMessage == null) {
                                listRemove.add(i);
                            }
                        }
                        ArrayList<QBChatDialog> result = new ArrayList<QBChatDialog>();
                        if(listRemove.size() == 0){
                            result = qbChatDialogs;
                        }
                        for (int i = 0; i < qbChatDialogs.size(); i++) {
                            for (int j = 0; j < listRemove.size(); j++) {
                                if (listRemove.get(j) != i) {
                                    result.add(qbChatDialogs.get(i));
                                }
                            }
                        }


                        subscriber.onNext(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public Observable<ArrayList<QBUser>> getQBUsersByID(ArrayList<Integer> listID) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<QBUser>>() {
            @Override
            public void call(Subscriber<? super ArrayList<QBUser>> subscriber) {
                QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
                pagedRequestBuilder.setPage(1);
                pagedRequestBuilder.setPerPage(50);
                QBUsers.getUsersByIDs(listID, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                        subscriber.onNext(qbUsers);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    //To listen for messages from any chat dialogs
    public Observable<QBChatMessage> receivingIcomingMessages() {
        return Observable.create(new Observable.OnSubscribe<QBChatMessage>() {
            @Override
            public void call(Subscriber<? super QBChatMessage> subscriber) {
                QBIncomingMessagesManager incomingMessagesManager = chatService.getIncomingMessagesManager();
                incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
                    @Override
                    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                        subscriber.onNext(qbChatMessage);
                    }

                    @Override
                    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                        LogUtil.error(e.getMessage());
                        subscriber.onError(new Throwable(e.getMessage()));
                    }
                });
            }
        });
    }

    public HashMap<String, QBChatMessage> getLastestMessages() {
        return listLastestMessages;
    }


    public Context getContext() {
        return context;
    }
}

