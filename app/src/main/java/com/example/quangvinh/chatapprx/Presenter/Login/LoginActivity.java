package com.example.quangvinh.chatapprx.Presenter.Login;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.IValidateInput;
import com.example.quangvinh.chatapprx.Helper.ValidateInput;
import com.example.quangvinh.chatapprx.Presenter.FindNearbyUser.FindUserActivity;
import com.example.quangvinh.chatapprx.Presenter.Register.RegisterActivity;
import com.example.quangvinh.chatapprx.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ViewLogin, IValidateInput, View.OnClickListener {
    @BindView(R.id.editTextUsername_Login)
    EditText edtUsername;
    @BindView(R.id.editTextPassword_Login)
    EditText edtPassword;
    @BindView(R.id.checkboxAutoLogin_Login)
    CheckBox checkBoxAutoLogin;
    @BindView(R.id.buttonLogin_Login)
    Button btnLogin;
    @BindView(R.id.textViewCreateAccount_Login)
    TextView tvCreateAccount;
    @BindView(R.id.textViewResetPassword_Login)
    TextView tvResetPassword;
    @BindView(R.id.textViewError_Login)
    TextView tvError;
    @BindView(R.id.progressBar_Login)
    ProgressBar progressBar;
    @BindView(R.id.textViewSendVerification_Login)
    TextView tvSendVerification;

    EditText edtEmailForget;
    EditText edtEmailReset;
    Dialog dialog;

    PresenterLogin presenterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ValidateInput validateInput = new ValidateInput(this);
        validateInput.validateUsername();
        validateInput.validatePassword();
        presenterLogin = new PresenterLogin(this, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        presenterLogin.initPresenterLogin(edtUsername, edtPassword, bundle);
        initView();
    }

    private void initView() {
        tvCreateAccount.setOnClickListener(this);
        tvResetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        edtEmailForget = new EditText(this);
        edtEmailForget.setSingleLine(true);
        tvSendVerification.setOnClickListener(this);
        checkBoxAutoLogin.setOnClickListener(this);
    }

    @Override
    public void validateUserName() {
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString();
                if (username == null) {
                    edtUsername.setError(getString(R.string.email_empty));
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    edtUsername.setError(getString(R.string.email_invalid));
                }
            }
        });
    }

    @Override
    public void validateFullName() {

    }

    @Override
    public void validatePassword() {
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (password == null) {
                    edtPassword.setError(getString(R.string.password_empty));
                } else if (password.length() < Const.MIN_PASSWORD_LENGTH) {
                    edtPassword.setError(getString(R.string.password_invalid));
                }
            }
        });
    }

    @Override
    public void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void enableInpuField() {
        edtPassword.setEnabled(true);
        edtUsername.setEnabled(true);
        tvCreateAccount.setEnabled(true);
        tvResetPassword.setEnabled(true);
        tvSendVerification.setEnabled(true);
        checkBoxAutoLogin.setEnabled(true);
    }

    @Override
    public void disableInputField() {
        edtPassword.setEnabled(false);
        edtUsername.setEnabled(false);
        tvCreateAccount.setEnabled(false);
        tvResetPassword.setEnabled(false);
        tvSendVerification.setEnabled(false);
        checkBoxAutoLogin.setEnabled(false);
    }

    @Override
    public void enableButtonLogin() {
        btnLogin.setEnabled(true);
    }

    @Override
    public void disableButtonLogin() {
        btnLogin.setEnabled(false);
    }

    @Override
    public void showSigninSuccess(String userName) {
        Toast.makeText(getApplicationContext(), "Hi " + userName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSigninError(String messageError) {
        if (TextUtils.isEmpty(messageError)) {
            tvError.setText("");
        } else {
            tvError.setText("Error: " + messageError);
        }
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
    public void setEmailField(String email) {
        edtUsername.setText(email);
    }

    @Override
    public void setPasswordField(String password) {
        edtPassword.setText(password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewCreateAccount_Login: {
                presenterLogin.goToRegister();
            }
            break;
            case R.id.buttonLogin_Login: {
                User user = new User();
                user.setEmail(edtUsername.getText().toString().trim());
                user.setPassword(edtPassword.getText().toString().trim());
                presenterLogin.signin(user, checkBoxAutoLogin.isChecked());
            }
            break;
            case R.id.textViewResetPassword_Login: {
                presenterLogin.showDialogResetPassword();
            }
            break;
            case R.id.buttonConfirm_Dialog: {
                presenterLogin.resetPassword(edtEmailReset.getText().toString().trim());
            }
            break;
            case R.id.textViewSendVerification_Login: {
                presenterLogin.sendVerification();
            }
            break;
            case R.id.checkboxAutoLogin_Login: {
                if (checkBoxAutoLogin.isChecked()) {
                    Const.REMEMBER_LOGIN = true;
                } else {
                    Const.REMEMBER_LOGIN = false;
                }
            }
            break;
        }
    }

    @Override
    public void showDialogForget() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        edtEmailReset = (EditText) dialog.findViewById(R.id.editTextReset_Dialog);
        edtEmailReset.setText(edtUsername.getText().toString().trim());
        Button btnConfirm = (Button) dialog.findViewById(R.id.buttonConfirm_Dialog);
        btnConfirm.setOnClickListener(this);
        dialog.show();
//        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
//        alertDialog.setTitle("Reset Password");
//        alertDialog.setMessage("Type your email to reset");
//        if (edtEmailForget.getParent() != null) {
//            ((ViewGroup) edtEmailForget.getParent()).removeView(edtEmailForget);
//        }
//        alertDialog.setView(edtEmailForget);
//        edtEmailForget.setText("");
//        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String email = edtEmailForget.getText().toString().trim();
//                presenterLogin.resetPassword(email);
//            }
//        });
//        alertDialog.show();
    }

    @Override
    public void showSendForgetSucess() {
        Toast.makeText(getApplicationContext(), "Check your email to reset password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeDialog() {
        dialog.dismiss();
    }

    @Override
    public void goToFindUser(Bundle user) {
        Intent intent = new Intent(LoginActivity.this, FindUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(user);
        startActivity(intent);
    }

    @Override
    public void showSendVerificationSuccessfully() {
        Toast.makeText(getApplicationContext(), "Verification is sent to your email", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTextViewSendVerification() {
        tvSendVerification.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTextViewSendVerification() {
        tvSendVerification.setVisibility(View.GONE);
    }


}
