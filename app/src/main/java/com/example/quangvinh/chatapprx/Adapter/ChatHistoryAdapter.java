package com.example.quangvinh.chatapprx.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quangvinh.chatapprx.Data.ChatDialogUI;
import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Presenter.ChatHistory.PresenterChatHistory;
import com.example.quangvinh.chatapprx.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QuangVinh on 4/2/2017.
 */

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.CustomViewHolder> {
    Context mContext;
    PresenterChatHistory presenterChatHistory;
    OnClickRecyclerView_ChatHistory mOnClickRecyclerView_chatHistory;

    public ChatHistoryAdapter(Context mContext, PresenterChatHistory presenterChatHistory) {
        this.mContext = mContext;
        this.presenterChatHistory = presenterChatHistory;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rowView = (View) LayoutInflater.from(mContext).inflate(R.layout.custom_chathistory, viewGroup, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(rowView);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        ChatDialogUI chatDialog = presenterChatHistory.getListChatDialogs().get(i);
        User receiver = chatDialog.getReceiver();
        User sender = chatDialog.getSender();
        if (receiver != null) {
            if (!receiver.getQbuserID().equals(presenterChatHistory.getMyQbUserId())) {
                customViewHolder.name.setText(receiver.getName());
                if (chatDialog.isHasNewMessage()) {
                    customViewHolder.notification.setVisibility(View.VISIBLE);
                } else {
                    customViewHolder.notification.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(receiver.getUrlImage()))
                    Picasso.with(mContext).load(receiver.getUrlImage()).placeholder(R.drawable.noimage).into(customViewHolder.avatar);
            }
        }
        if(sender != null){
            if (!sender.getQbuserID().equals(presenterChatHistory.getMyQbUserId())) {
                customViewHolder.name.setText(sender.getName());
                if (chatDialog.isHasNewMessage()) {
                    customViewHolder.notification.setVisibility(View.VISIBLE);
                } else {
                    customViewHolder.notification.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(sender.getUrlImage()))
                    Picasso.with(mContext).load(receiver.getUrlImage()).placeholder(R.drawable.noimage).into(customViewHolder.avatar);
            }
        }
    }


    @Override
    public int getItemCount() {
        return presenterChatHistory.getListChatDialogs().size();
    }

    public void setOnClickRecyclerView_chatHistory(OnClickRecyclerView_ChatHistory onClickRecyclerView_chatHistory) {
        mOnClickRecyclerView_chatHistory = onClickRecyclerView_chatHistory;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView avatar;
        TextView name;
        TextView notification;

        public CustomViewHolder(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.circleImageViewAvatar_ChatHistory);
            name = (TextView) itemView.findViewById(R.id.textViewName_ChatHistory);
            notification = (TextView) itemView.findViewById(R.id.textViewNotification_ChatHistory);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mOnClickRecyclerView_chatHistory.onItemClick(getAdapterPosition());
        }
    }

    public interface OnClickRecyclerView_ChatHistory {
        public void onItemClick(int position);
    }
}
