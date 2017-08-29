package com.example.quangvinh.chatapprx.Presenter.ChatHistory;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.quangvinh.chatapprx.Adapter.ChatHistoryAdapter;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Presenter.Chat.ChatActivity;
import com.example.quangvinh.chatapprx.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatHistory extends AppCompatActivity implements ViewChatHistory {
    @BindView(R.id.recyclerView_ChatHistory)
    RecyclerView recyclerView;
    ChatHistoryAdapter chatHistoryAdapter;
    PresenterChatHistory presenterChatHistory;
    @BindView(R.id.progressBar_ChatHistory)
    ProgressBar progressBar;
    @BindView(R.id.textViewNoConversation)
    TextView tvNoconversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        ButterKnife.bind(this);
        presenterChatHistory = new PresenterChatHistory(this, this);
        initView();
        Intent intent = getIntent();
        presenterChatHistory.initPresenter(intent.getExtras());
    }

    private void initView() {
        chatHistoryAdapter = new ChatHistoryAdapter(this, presenterChatHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatHistoryAdapter);
        chatHistoryAdapter.setOnClickRecyclerView_chatHistory(new ChatHistoryAdapter.OnClickRecyclerView_ChatHistory() {
            @Override
            public void onItemClick(int position) {
                presenterChatHistory.goToChat(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenterChatHistory.markNewDialog();
    }

    @Override
    public void refreshRecyclerView() {
        chatHistoryAdapter.notifyDataSetChanged();
        hideProgressbar();
    }

    @Override
    public void goToHistory(Bundle bundle) {
        Intent intent = new Intent(ChatHistory.this, ChatActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showProgressbar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNoConversation() {
        tvNoconversation.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoConversation() {
        tvNoconversation.setVisibility(View.GONE);
    }
}
