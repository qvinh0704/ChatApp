package com.example.quangvinh.chatapprx.Presenter.Chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quangvinh.chatapprx.Adapter.ChatAdapter;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.HelperDevice;
import com.example.quangvinh.chatapprx.Helper.LogUtil;
import com.example.quangvinh.chatapprx.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements ViewChat, View.OnClickListener {
    @BindView(R.id.recyclerView_Messages_Chat)
    RecyclerView recyclerView_Messages;
    @BindView(R.id.imageButtonSend_Chat)
    ImageButton btnSend;
    @BindView(R.id.editText_Message_Chat)
    EditText edtMessage;
    @BindView(R.id.toolbar_Chat)
    Toolbar toolbar;
    @BindView(R.id.progressBar_Chat)
    ProgressBar progressBar;
    @BindView(R.id.relative_Error_Chat)
    RelativeLayout relativeLayoutError;
    PresenterChat presenterChat;
    ChatAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        presenterChat = new PresenterChat(this, this);
        initView();
        Intent intent = getIntent();
        presenterChat.initPresenterChat(intent.getExtras(), edtMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HelperDevice.stopVibrate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HelperDevice.initialVibrate(getApplicationContext());
    }

    private void initView() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnSend.setOnClickListener(this);
        adapter = new ChatAdapter(this, presenterChat);
        recyclerView_Messages.setLayoutManager(linearLayoutManager);
        recyclerView_Messages.setAdapter(adapter);
        edtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                presenterChat.markReadMessage();
                return false;
            }
        });
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonSend_Chat: {
                String content = edtMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    edtMessage.setText("");
                    presenterChat.sendMessage(content);
                }
            }
            break;
        }
    }

    @Override
    public void refreshRecyclerView() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() != 0)
            recyclerView_Messages.smoothScrollToPosition(adapter.getItemCount() - 1);
        checkLoadmoreMessage();
        hideProgressbar();
    }

    @Override
    public void showNameInToolbar(String name) {
        getSupportActionBar().setTitle(name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
    public void loadMoreMessage(int currentPosition) {
        adapter.notifyDataSetChanged();
//        recyclerView_Messages.smoothScrollToPosition(currentPosition);
        hideProgressbar();
        recyclerView_Messages.setEnabled(true);
    }

    @Override
    public void checkLoadmoreMessage() {
        recyclerView_Messages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                    recyclerView.setEnabled(false);
                    presenterChat.loadMoreMessages();
                    LogUtil.debug("top of recycler view");
                }
            }
        });
    }

    @Override
    public void enableTypeMessage() {
        edtMessage.setEnabled(true);
        btnSend.setEnabled(true);
    }

    @Override
    public void disableTypeMessage() {
        edtMessage.setEnabled(false);
        btnSend.setEnabled(false);
    }

    @Override
    public void showError() {
        relativeLayoutError.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideError() {
        relativeLayoutError.setVisibility(View.GONE);
    }
}
