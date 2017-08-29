package com.example.quangvinh.chatapprx.Presenter.Register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import com.example.quangvinh.chatapprx.Presenter.Login.LoginActivity;
import com.example.quangvinh.chatapprx.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements ViewRegister,View.OnClickListener,IValidateInput {
    @BindView(R.id.editTextEmail_Register)
    EditText edtEmail;
    @BindView(R.id.editTextFullname_Register)
    EditText edtFullname;
    @BindView(R.id.editTextPassword_Register)
    EditText edtPassword;
    @BindView(R.id.buttonRegister_Register)
    Button btnRegister;
    @BindView(R.id.progressBar_Register)
    ProgressBar progressBar;
    @BindView(R.id.radioButtonFemale)
    RadioButton radioButtonFemale;
    @BindView(R.id.radioButtonMale)
    RadioButton radioButtonMale;
    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;
    @BindView(R.id.textViewError)
    TextView tvError;

    PresenterRegister presenterRegister;
    private int REQUEST_CODE_IMAGE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        presenterRegister = new PresenterRegister(this, this);
        ValidateInput validateInput = new ValidateInput(this);
        validateInput.validatePassword();
        validateInput.validateUsername();
        validateInput.validateFullname();
        presenterRegister.validateButtonRegister(edtEmail,edtFullname,edtPassword);

        initView();

    }

    private void initView() {
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_IMAGE);
            }
        });
        btnRegister.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null){
            presenterRegister.intentToBitmap(data);
        }
    }

    @Override
    public void validateUserName() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (email == null) {
                    edtEmail.setError(getString(R.string.email_empty));
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError(getString(R.string.email_invalid));
                }
            }
        });
    }

    @Override
    public void validateFullName() {
        edtFullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String fullname = s.toString();
                if (fullname == null) {
                    edtFullname.setError(getString(R.string.name_empty));
                } else if (fullname.length() < Const.MIN_FULLNAME_LENGTH) {
                    edtFullname.setError(getString(R.string.name_invalid));
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
    public void enableButtonRegister() {
        btnRegister.setEnabled(true);
    }

    @Override
    public void disableButtonRegister() {
        btnRegister.setEnabled(false);
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
    public void enableInputField() {
        edtEmail.setEnabled(true);
        edtFullname.setEnabled(true);
        edtPassword.setEnabled(true);
        radioButtonFemale.setEnabled(true);
        radioButtonMale.setEnabled(true);
        imageViewAvatar.setEnabled(true);
    }

    @Override
    public void disableInputField() {
        edtEmail.setEnabled(false);
        edtFullname.setEnabled(false);
        edtPassword.setEnabled(false);
        radioButtonFemale.setEnabled(false);
        radioButtonMale.setEnabled(false);
        imageViewAvatar.setEnabled(false);
    }

    @Override
    public void showRegisterSucess() {
        Toast.makeText(getApplicationContext(), "Verification is sent to your email !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRegisterFailed(String messageError) {
        if(TextUtils.isEmpty(messageError)){
            tvError.setText("");
        }else{
            tvError.setText("Error: "+messageError);
        }
    }

    @Override
    public void goToLogin(Bundle user) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtras(user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setAvatarToImageview(Bitmap bitmap) {
        imageViewAvatar.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonRegister_Register:{
                String male = presenterRegister.getSex(radioButtonMale.isChecked());
                User user = new User(edtFullname.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),
                        edtPassword.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),
                        edtPassword.getText().toString().trim(),
                        male
                        );
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageViewAvatar.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                presenterRegister.setFileAvatar(bitmap);
                presenterRegister.signUp(user);
            }break;
            default:break;
        }
    }

    @Override
    public void showErrorNetworkConnection() {
        tvError.setText("Error: Check your network conection.");
    }

    @Override
    public void clearPassword() {
        edtPassword.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
