package com.example.quangvinh.chatapprx.Helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.quangvinh.chatapprx.Data.ChatDialog;
import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.Message;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Data.User;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.quickblox.chat.model.QBChatMessage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by QuangVinh on 3/21/2017.
 */

public class HelperTransformation {
    public static File changeBitmapToFile(Bitmap bitmap, Activity mActivity) throws IOException {
        File avatar;
        Bitmap bmScale = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        avatar = new File(mActivity.getCacheDir(), String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmScale.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        FileOutputStream fileOutputStream = new FileOutputStream(avatar);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
        return avatar;
    }

    public static Bitmap intentToBitmap(Intent intent, Activity mActivity) throws FileNotFoundException {
        Uri uri = intent.getData();
        InputStream inputStream = null;
        inputStream = mActivity.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public static MessageUI changeQBChatMessageToMessageUI(QBChatMessage qbChatMessage){
        Gson gson = new Gson();
        MessageUI message = new MessageUI();
        try{
            message = gson.fromJson(qbChatMessage.getBody(),MessageUI.class);
            message.setId(qbChatMessage.getId());
            return message;
        }catch (JsonParseException exception){
            LogUtil.error(exception.getMessage());
        }
        return null;
    }

    public static MessageUI changeMessge_To_MessageUI(Message message, User sender, User receiver){
        MessageUI messageUI = new MessageUI();
        messageUI.setDialogID(message.getDialogID());
        messageUI.setContent(message.getContent());
        messageUI.setId(message.getId());
        messageUI.setStatus(message.getStatus());
        if(sender != null)
            messageUI.setSender(sender);
        else{
            User tempSender = new User();
            tempSender.setQbuserID(message.getSenderID());
            messageUI.setSender(tempSender);
        }
        if(receiver != null)
            messageUI.setReceiver(receiver);
        else{
            User tempReceiver = new User();
            tempReceiver.setQbuserID(message.getReceiverID());
            messageUI.setReceiver(tempReceiver);
        }
        return messageUI;
    }

    public static Message changeMessageUI_To_Message(MessageUI messageUI){
        Message message = new Message();
        message.setContent(messageUI.getContent());
        message.setId(messageUI.getId());
        message.setReceiverID(messageUI.getReceiver().getQbuserID());
        message.setSenderID(messageUI.getSender().getQbuserID());
        message.setStatus(messageUI.getStatus());
        message.setDialogID(messageUI.getDialogID());
        return message;
    }

    public static ChatDialogUI changeChatDialog_To_ChatDialogUI(ChatDialog chatDialog,User sender,User receiver){
        ChatDialogUI chatDialogUI = new ChatDialogUI();
        chatDialogUI.setDialogID(chatDialog.getDialogID());
        chatDialogUI.setHasNewMessage(chatDialog.isHasNewMessage());
        if(sender != null)
            chatDialogUI.setSender(sender);
        else {
            User tempSender = new User();
            tempSender.setQbuserID(chatDialog.getSenderID());
            chatDialogUI.setSender(tempSender);
        }
        if(receiver != null)
            chatDialogUI.setReceiver(receiver);
        else {
            User tempReceiver = new User();
            tempReceiver.setQbuserID(chatDialog.getReceiverID());
            chatDialogUI.setReceiver(tempReceiver);
        }
        return chatDialogUI;
    }

    public static ChatDialog changeChatDialogUI_To_ChatDialog(ChatDialogUI chatDialogUI){
        ChatDialog chatDialog = new ChatDialog();
        chatDialog.setDialogID(chatDialogUI.getDialogID());
        chatDialog.setHasNewMessage(chatDialogUI.isHasNewMessage());
        chatDialog.setReceiverID(chatDialogUI.getReceiver().getQbuserID());
        chatDialog.setSenderID(chatDialogUI.getSender().getQbuserID());
        return chatDialog;
    }

    public static QBChatMessage changeMessageUI_To_QBChatMessage(MessageUI messageUI){
        Gson gson = new Gson();
        String body = gson.toJson(messageUI);
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setSenderId(Integer.valueOf(messageUI.getSender().getQbuserID()));
        qbChatMessage.setRecipientId(Integer.valueOf(messageUI.getReceiver().getQbuserID()));
        qbChatMessage.setDialogId(messageUI.getDialogID());
        qbChatMessage.setBody(body);
        qbChatMessage.setId(messageUI.getId());
        qbChatMessage.setSaveToHistory(true);
        return qbChatMessage;
    }
}
