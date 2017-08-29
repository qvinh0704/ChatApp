package com.example.quangvinh.chatapprx.Helper;

/**
 * Created by QuangVinh on 3/17/2017.
 */

public class ValidateInput {
    private IValidateInput iValidateInput;

    public ValidateInput(IValidateInput tempIValidateInput){
        iValidateInput = tempIValidateInput;
    }

    public void validateUsername(){
        iValidateInput.validateUserName();
    }

    public void validatePassword(){
        iValidateInput.validatePassword();
    }

    public void validateFullname(){
        iValidateInput.validateFullName();
    }
}
