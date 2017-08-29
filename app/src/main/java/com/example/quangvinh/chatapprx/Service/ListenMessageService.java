package com.example.quangvinh.chatapprx.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperDevice;
import com.example.quangvinh.chatapprx.Helper.HelperTransformation;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Presenter.Chat.ChatActivity;
import com.example.quangvinh.chatapprx.Presenter.Loading.LoadingActivity;
import com.example.quangvinh.chatapprx.R;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.quickblox.chat.model.QBChatMessage;
import com.squareup.otto.Bus;

import rx.Subscriber;

/**
 * Created by QuangVinh on 4/18/2017.
 */

public class ListenMessageService extends Service {
    IBinder iBinder = new MyBinder();
    public static Bus bus = new Bus();
    QuickbloxService quickbloxService;
    SQLiteService sqLiteService;
    Activity mActivity;
    RealmService realmService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.iBinder;
    }

    public ListenMessageService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        realmService = RealmService.getRealmService(getApplicationContext());
        quickbloxService = QuickbloxService.getQuickblox_Instance(getApplicationContext());
        sqLiteService = new SQLiteService(getApplicationContext(), Const.SQLITE_DATABASE_NAME, null, 1);
        quickbloxService.receivingIcomingMessages().subscribe(new Subscriber<QBChatMessage>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

                LogUtil.error(e.getMessage());
            }

            @Override
            public void onNext(QBChatMessage qbChatMessage) {
                MessageUI message = HelperTransformation.changeQBChatMessageToMessageUI(qbChatMessage);
                saveMessageToLocal(message, qbChatMessage);
                saveDialogToLocal(message);
                HelperDevice.vibrateDevice();
                bus.post(message);
                pushNotification(message);
//                showNewMessage();
//                pushNotification();
                if (quickbloxService.getLastestMessages().size() == 0) {
                    try {
                        Badges.removeBadge(getApplicationContext());
                    } catch (BadgesNotSupportedException badgesNotSupportedException) {
                        LogUtil.error(badgesNotSupportedException.getMessage());
                    }
                } else {
                    int count = quickbloxService.getLastestMessages().size();
                    try {
                        Badges.setBadge(getApplicationContext(), count);
                    } catch (BadgesNotSupportedException badgesNotSupportedException) {
                        LogUtil.error(badgesNotSupportedException.getMessage());
                    }
                }
            }
        });
        return START_STICKY;
    }

    private void saveMessageToLocal(MessageUI message, QBChatMessage qbChatMessage) {
        sqLiteService.createTableMessage();
//        sqLiteService.addMessage(message);
        realmService.addMessage(message);
        quickbloxService.getLastestMessages().put(qbChatMessage.getDialogId(), qbChatMessage);
        LogUtil.debug(quickbloxService.getLastestMessages().get(qbChatMessage.getDialogId()).getBody());
    }

    private void saveDialogToLocal(MessageUI message) {
        sqLiteService.createTableDialog();
        ChatDialogUI chatDialog = new ChatDialogUI();
        chatDialog.setDialogID(message.getDialogID());
        chatDialog.setSender(message.getSender());
        chatDialog.setReceiver(message.getReceiver());
//        boolean checkDialog = sqLiteService.checkDiaglogExist(chatDialog.getDialogID());
        boolean checkDialog = realmService.checkDiaglogExist(chatDialog.getDialogID());
        if (checkDialog == false)
//            sqLiteService.addChatDialog(chatDialog);
            realmService.addChatDialog(chatDialog);
    }

    public void pushNotification(MessageUI message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.BUNDLE_RECEIVER_FINDUSER,message.getReceiver());
        bundle.putSerializable(Const.BUNDLE_USER_FROM_FINDUSER,message.getSender());
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText("You have a new message")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.purplemarker)
                .build();
        notificationManager.notify(0, notification);
    }

    public class MyBinder extends Binder {
        public ListenMessageService getService() {
            return ListenMessageService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.debug("Destroy service");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
