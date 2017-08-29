package com.example.quangvinh.chatapprx.Presenter.Chat;

/**
 * Created by QuangVinh on 3/27/2017.
 */

public interface ViewChat {
    public void showMessage(String message);
    public void refreshRecyclerView();
    public void showNameInToolbar(String name);
    public void showProgressbar();
    public void hideProgressbar();
    public void loadMoreMessage(int currentPosition);
    public void checkLoadmoreMessage();
    public void enableTypeMessage();
    public void disableTypeMessage();
    public void showError();
    public void hideError();
}
