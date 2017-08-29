package com.example.quangvinh.chatapprx.Presenter.ChatHistory;

import android.app.Activity;
import android.os.Bundle;

import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.ModelChatHistory;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Service.FirebaseData;
import com.example.quangvinh.chatapprx.Service.ListenMessageService;
import com.example.quangvinh.chatapprx.Service.QuickbloxService;
import com.example.quangvinh.chatapprx.Service.RealmService;
import com.example.quangvinh.chatapprx.Service.SQLiteService;
import com.quickblox.chat.model.QBChatDialog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by QuangVinh on 3/30/2017.
 */

public class PresenterChatHistory implements PresenterImpChatHistory {
    private ModelChatHistory modelChatHistory;
    private ViewChatHistory mViewChatHistory;
    private QuickbloxService quickbloxService;
    private FirebaseData mFirebaseData;
    private SQLiteService sqLiteService;
    private RealmService realmService;

    public PresenterChatHistory(Activity activity, ViewChatHistory viewChatHistory) {
        this.mViewChatHistory = viewChatHistory;
        quickbloxService = QuickbloxService.getQuickblox_Instance(activity);
        mFirebaseData = new FirebaseData();
        modelChatHistory = new ModelChatHistory();
        sqLiteService = new SQLiteService(activity, Const.SQLITE_DATABASE_NAME, null, 1);
        realmService = RealmService.getRealmService(activity.getApplicationContext());
    }

    @Override
    public void initPresenter(Bundle bundle) {
        getBundleFromFindUser(bundle);
        sqLiteService.createTableDialog();
        if (ListenMessageService.bus != null) {
            ListenMessageService.bus.register(this);
        }
//        ArrayList<ChatDialogUI> chatDialogs = sqLiteService.getListChatDialogBySenderID(quickbloxService.getQbUser().getId().toString());
        ArrayList<ChatDialogUI> chatDialogs = realmService.getListChatDialogBySenderID(quickbloxService.getQbUser().getId().toString());
        //Get dialog from sqlite
        modelChatHistory.setListChatDialog(chatDialogs);
        if(chatDialogs.size() != 0 )
            markNewDialog();

        //Get dialog from quickblox
        getDialogsFromQuickblox();
    }

    @Override
    public ArrayList<ChatDialogUI> getListChatDialogs() {
        return modelChatHistory.getListChatDialog();
    }

    @Override
    public void getDialogsFromQuickblox() {
        quickbloxService.getDialogs()
                .subscribe(new Subscriber<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.error(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<QBChatDialog> qbChatDialogs) {
                        if(qbChatDialogs.size() == 0)
                            mViewChatHistory.showNoConversation();
                        else
                            mViewChatHistory.hideNoConversation();
                        addListDialog(qbChatDialogs);
                        ArrayList<Integer> listId = getListId_From_ListQBChatDialog(qbChatDialogs);
                        for (int i = 0; i < listId.size(); i++) {
                            getUserByQbUserID(listId.get(i).toString());
                        }
                    }
                });
    }

    private void getUserByQbUserID(String id) {
        mFirebaseData.getUserByQBUserID(id)
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        for (int i = 0; i < modelChatHistory.getListChatDialog().size(); i++) {
                            if (modelChatHistory.getListChatDialog().get(i).getReceiver().getQbuserID().equals(user.getQbuserID())) {
                                modelChatHistory.getListChatDialog().get(i).setReceiver(user);
//                                if (sqLiteService.checkDiaglogExist(modelChatHistory.getListChatDialog().get(i).getDialogID()) == false)
//                                    sqLiteService.addChatDialog(modelChatHistory.getListChatDialog().get(i));
                                if(realmService.checkDiaglogExist(modelChatHistory.getListChatDialog().get(i).getDialogID()) == false)
                                    realmService.addChatDialog(modelChatHistory.getListChatDialog().get(i));
                            }
                        }
                        markNewDialog();
                        mViewChatHistory.refreshRecyclerView();
                    }
                });
    }

    private ArrayList<Integer> getListId_From_ListQBChatDialog(ArrayList<QBChatDialog> listQBChatDialog) {
        ArrayList<Integer> listId = new ArrayList<Integer>();
        for (int i = 0; i < listQBChatDialog.size(); i++) {
            ChatDialogUI chatDialog = getChatDialogByID_FromModel(listQBChatDialog.get(i).getDialogId());
            for (int j = 0; j < listQBChatDialog.get(i).getOccupants().size(); j++) {
                User receiver = new User();
                Integer idOccupant = listQBChatDialog.get(i).getOccupants().get(j);
                Integer idMyUser = quickbloxService.getQbUser().getId();
                if (!idOccupant.equals(idMyUser)) {
                    listId.add(idOccupant);
                    receiver.setQbuserID(idOccupant.toString());
                    chatDialog.setReceiver(receiver);
                } else {
                    chatDialog.setSender(modelChatHistory.getmUser());
                }
            }
        }
        return listId;
    }

    private ChatDialogUI getChatDialogByID_FromModel(String id) {
        for (int i = 0; i < modelChatHistory.getListChatDialog().size(); i++) {
            if (modelChatHistory.getListChatDialog().get(i).getDialogID().equals(id)) {
                return modelChatHistory.getListChatDialog().get(i);
            }
        }
        return null;
    }

    private void addListDialog(ArrayList<QBChatDialog> listQBChatDialog) {
        ArrayList<ChatDialogUI> listChatdialogs = new ArrayList<>();
        for (int i = 0; i < listQBChatDialog.size(); i++) {
            ChatDialogUI temp = new ChatDialogUI();
            temp.setDialogID(listQBChatDialog.get(i).getDialogId());
            listChatdialogs.add(temp);
        }
        modelChatHistory.setListChatDialog(listChatdialogs);
    }

    @Override
    public void markNewDialog() {
        ArrayList<ChatDialogUI> listDialog = modelChatHistory.getListChatDialog();
        for (int i = 0; i < listDialog.size(); i++) {
            String dialogID = listDialog.get(i).getDialogID();
            if (quickbloxService.getLastestMessages().containsKey(dialogID)) {
                listDialog.get(i).setHasNewMessage(true);
            } else {
                listDialog.get(i).setHasNewMessage(false);
            }
        }
        sortDialog();
        mViewChatHistory.refreshRecyclerView();
    }

    private void sortDialog() {
        ArrayList<ChatDialogUI> listDialog = modelChatHistory.getListChatDialog();
        for(int i = 0; i < listDialog.size();i++){
            if(listDialog.get(i).isHasNewMessage()){
                //Swap
                for(int j = 0; j < i;j++){
                    if(listDialog.get(j).isHasNewMessage() == false){
                        ChatDialogUI temp = listDialog.get(j);
                        listDialog.set(j,listDialog.get(i));
                        listDialog.set(i,temp);
                    }
                }
            }
        }
        mViewChatHistory.refreshRecyclerView();
    }

    @Override
    public void goToChat(int position) {
        Bundle bundle = new Bundle();
        ChatDialogUI chatDialog = modelChatHistory.getListChatDialog().get(position);
        bundle.putSerializable(Const.BUNDLE_USER_FROM_FINDUSER, chatDialog.getSender());
        bundle.putSerializable(Const.BUNDLE_RECEIVER_FINDUSER, chatDialog.getReceiver());
        mViewChatHistory.goToHistory(bundle);
    }

    @Subscribe
    public void getMessage(MessageUI message) {
//        ArrayList<ChatDialogUI> list = sqLiteService.getListChatDialogBySenderID(modelChatHistory.getmUser().getQbuserID());
        ArrayList<ChatDialogUI> list = realmService.getListChatDialogBySenderID(modelChatHistory.getmUser().getQbuserID());
        modelChatHistory.setListChatDialog(list);
        if(modelChatHistory.getListChatDialog().size() != 0)
            mViewChatHistory.hideNoConversation();
        markNewDialog();
    }

    @Override
    public String getMyQbUserId() {
        return quickbloxService.getQbUser().getId() + "";
    }

    @Override
    public void getBundleFromFindUser(Bundle bundle) {
        if(bundle != null){
            User mUser = (User) bundle.getSerializable(Const.BUNDLE_USER_FROM_FINDUSER);
            if(mUser != null){
                modelChatHistory.setmUser(mUser);
            }
        }
    }
}
