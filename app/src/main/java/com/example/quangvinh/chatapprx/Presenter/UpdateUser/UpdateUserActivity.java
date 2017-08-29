package com.example.quangvinh.chatapprx.Presenter.UpdateUser;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quangvinh.chatapprx.Data.User;
import com.example.quangvinh.chatapprx.Helper.Const;
import com.example.quangvinh.chatapprx.Helper.IValidateInput;
import com.example.quangvinh.chatapprx.Helper.ValidateInput;
import com.example.quangvinh.chatapprx.Presenter.FindNearbyUser.FindUserActivity;
import com.example.quangvinh.chatapprx.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateUserActivity extends AppCompatActivity implements ViewUpdateUser, IValidateInput, View.OnClickListener {
    @BindView(R.id.editTextFullname_Update)
    EditText edtName;
    @BindView(R.id.editTextPassword_Update)
    EditText edtPassword;
    @BindView(R.id.imageViewAvatar_Update)
    ImageView imageViewAvatar;
    @BindView(R.id.buttonSave_Update)
    Button btnSave;
    @BindView(R.id.textViewError_Update)
    TextView tvError;
    @BindView(R.id.progressBar_Update)
    ProgressBar progressBar;
    @BindView(R.id.radioButtonFemale_Update)
    RadioButton radFemale;
    @BindView(R.id.radioButtonMale_Update)
    RadioButton radMale;
    @BindView(R.id.buttonTestDownAvatar)
    Button btnDown;

    EditText edtCurrentPassword;
    Button btnVerifyPassword;

    PresenterUpdateUser presenterUpdateUser;
    private final int REQUEST_CODE_IMAGE = 111;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        presenterUpdateUser = new PresenterUpdateUser(this, this);
        presenterUpdateUser.getUserFromFindUser(intent.getExtras());
        presenterUpdateUser.loadProfile();
        presenterUpdateUser.validateButtonSave(edtName, edtPassword);
        ValidateInput validateInput = new ValidateInput(this);
        validateInput.validateFullname();
        validateInput.validatePassword();
        initView();
    }

    private void initView() {
        btnSave.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        imageViewAvatar.setOnClickListener(this);
    }

    @Override
    public void enableButtonSave() {
        btnSave.setEnabled(true);
    }

    @Override
    public void disableButtonSave() {
        btnSave.setEnabled(false);
    }

    @Override
    public void showUpdateUserSuccess() {
        Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdateFailed(String message) {
        if (TextUtils.isEmpty(message)) {
            tvError.setText("");
        } else {
            tvError.setText("Error: " + message);
        }
    }

    @Override
    public void enableInputField() {
        edtName.setEnabled(true);
        edtPassword.setEnabled(true);
        imageViewAvatar.setEnabled(true);
        radFemale.setEnabled(true);
        radMale.setEnabled(true);
    }

    @Override
    public void disableInputField() {
        edtName.setEnabled(false);
        edtPassword.setEnabled(false);
        imageViewAvatar.setEnabled(false);
        radFemale.setEnabled(false);
        radMale.setEnabled(false);
    }

    @Override
    public void showProfileUser(User user) {
        edtName.setText(user.getName());
        if (!TextUtils.isEmpty(user.getUrlImage())) {
            Picasso.with(this).load(user.getUrlImage()).placeholder(R.drawable.noimage).into(imageViewAvatar);
        }
        if (user.getSex().equals("Male")) {
            radMale.setChecked(true);
        } else {
            radFemale.setChecked(true);
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
    public void showGetProfileFailed() {
        Toast.makeText(getApplicationContext(), "Can not load profile", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void validateUserName() {
    }

    @Override
    public void validateFullName() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                if (name == null) {
                    edtName.setError(getString(R.string.name_empty));
                } else if (name.length() < Const.MIN_FULLNAME_LENGTH) {
                    edtName.setError(getString(R.string.name_invalid));
                }
            }
        });
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
                String pass = s.toString();
                if (pass == null) {
                    edtPassword.setError(getString(R.string.password_empty));
                } else if (pass.length() < Const.MIN_PASSWORD_LENGTH) {
                    edtPassword.setError(getString(R.string.password_invalid));
                }
            }
        });
    }

    @Override
    public void showDiaglogVerify() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_updateuser);
        btnVerifyPassword = (Button) dialog.findViewById(R.id.buttonConfirm_Dialog_Update);
        btnVerifyPassword.setOnClickListener(this);
        edtCurrentPassword = (EditText) dialog.findViewById(R.id.editTextCurrPass_Dialog_Update);
        dialog.show();
    }

    @Override
    public void showVerifyUserFailed() {
        Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonConfirm_Dialog_Update: {
                User user = new User();
                user.setPassword(edtPassword.getText().toString().trim());
                user.setName(edtName.getText().toString().trim());
                if (radFemale.isChecked()) {
                    user.setSex("Female");
                } else {
                    user.setSex("Male");
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageViewAvatar.getDrawable();
                presenterUpdateUser.setFileAvatar(bitmapDrawable.getBitmap());
                presenterUpdateUser.requestUpdateUser(user, edtCurrentPassword.getText().toString().trim());
            }
            break;
            case R.id.imageViewAvatar_Update: {
                presenterUpdateUser.markAvatarChanged();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
            break;
            case R.id.buttonSave_Update: {
                presenterUpdateUser.showDiaglogVerify();
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            presenterUpdateUser.changeIntentToBitmap(data);
        }
    }

    @Override
    public void showAvatarToImageview(Bitmap bitmap) {
        imageViewAvatar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
    }

    @Override
    public void closeDialog() {
        dialog.dismiss();
    }

    @Override
    public void goToFindUser(Bundle user) {
        Intent intent = new Intent(UpdateUserActivity.this, FindUserActivity.class);
        intent.putExtras(user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
