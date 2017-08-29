package com.example.quangvinh.chatapprx.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quangvinh.chatapprx.Data.Message;
import com.example.quangvinh.chatapprx.Data.MessageUI;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.Presenter.Chat.PresenterChat;
import com.example.quangvinh.chatapprx.R;


/**
 * Created by QuangVinh on 3/27/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    PresenterChat presenterChat;
    private final Integer MY_MESSAGE = 1;
    private final Integer ANOTHER_MESSAGE = 0;
    private final Integer IS_TYPING = 2;

    public ChatAdapter(Context mContext, PresenterChat presenterChat) {
        this.mContext = mContext;
        this.presenterChat = presenterChat;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder reViewHolder = null;
        if (i == MY_MESSAGE) {
            View rowView = LayoutInflater.from(mContext).inflate(R.layout.my_message, viewGroup, false);
            reViewHolder = new CustomViewHoler_Mine(rowView);
        } else if(i == ANOTHER_MESSAGE){
            View rowView = LayoutInflater.from(mContext).inflate(R.layout.another_message, viewGroup, false);
            reViewHolder = new CustomViewHoler_Another(rowView);
        }else if(i == IS_TYPING){
            View rowView = LayoutInflater.from(mContext).inflate(R.layout.typing_message, viewGroup, false);
            reViewHolder = new CustomViewHoler_Typing(rowView);
        }
        return reViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        MessageUI message = presenterChat.getListMessageFromModel().get(i);
        int typeView = getItemViewType(i);
        if (typeView == MY_MESSAGE) {
            CustomViewHoler_Mine customViewHoler = (CustomViewHoler_Mine) viewHolder;
//            LogUtil.debug("Typeview " + typeView + "  Mine " + message.getRecipientID() + " -- " + presenterChat.getRecipientID());
            customViewHoler.message.setText(message.getContent());
            customViewHoler.status.setVisibility(View.GONE);
            int lastPosition = presenterChat.getListMessageFromModel().size() - 1;
            String status = message.getStatus();
            if (i == lastPosition && status != null) {
                if(!TextUtils.isEmpty(status)){
                    customViewHoler.status.setText(message.getStatus());
                    customViewHoler.status.setVisibility(View.VISIBLE);
                }
            }
        } else if(typeView == ANOTHER_MESSAGE){
            CustomViewHoler_Another customViewHoler = (CustomViewHoler_Another) viewHolder;
//            LogUtil.debug(("Typeview " + typeView + "  Another " + message.getRecipientID() + " -- " + presenterChat.getRecipientID()));
            customViewHoler.message.setText(message.getContent());
        }else if(typeView == IS_TYPING){
            CustomViewHoler_Typing customViewHoler = (CustomViewHoler_Typing) viewHolder;
        }

    }

    @Override
    public int getItemCount() {
        return presenterChat.getListMessageFromModel().size();
    }


    @Override
    public int getItemViewType(int position) {
        int recipientFromMessage = Integer.valueOf(presenterChat.getListMessageFromModel().get(position).getReceiver().getQbuserID());
        int recipientID = presenterChat.getRecipientID();
        String checkTyping = presenterChat.getListMessageFromModel().get(position).getId();
        LogUtil.debug(checkTyping);
        if (recipientFromMessage == recipientID) {
//            LogUtil.debug("Message: "+recipientFromMessage+" -- rID: "+recipientID+" --> Type: " +ANOTHER_MESSAGE);
            return MY_MESSAGE;
        } else if (checkTyping.equals(Const.ID_MESSAGE_TYPING)) {
            return IS_TYPING;
        } else {
//            LogUtil.debug("Message: "+recipientFromMessage+" -- rID: "+recipientID+" --> Type: " +MY_MESSAGE);
            return ANOTHER_MESSAGE;
        }

    }

    public class CustomViewHoler_Mine extends RecyclerView.ViewHolder {
        TextView message;
        TextView status;

        public CustomViewHoler_Mine(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.textViewMessage_Mine);
            status = (TextView) itemView.findViewById(R.id.textViewStatus_Mine);
        }
    }

    public class CustomViewHoler_Another extends RecyclerView.ViewHolder {
        TextView message;

        public CustomViewHoler_Another(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.textViewMessage_Another);
        }
    }

    public class CustomViewHoler_Typing extends RecyclerView.ViewHolder {
        TextView message_typing;

        public CustomViewHoler_Typing(View itemView) {
            super(itemView);
            message_typing = (TextView) itemView.findViewById(R.id.textView_IsTyping);
        }
    }
}
