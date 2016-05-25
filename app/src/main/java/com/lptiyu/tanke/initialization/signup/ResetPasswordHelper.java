package com.lptiyu.tanke.initialization.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;

import com.lptiyu.tanke.R;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class ResetPasswordHelper extends SignUpHelper {


    public ResetPasswordHelper(AppCompatActivity activity, View view) {
        super(activity, view);

    }

    @Override
    protected void init() {
        super.init();
        signUpTitle.setText(R.string.reset_password_title);
        signUpNextButton.setText(R.string.ensure);

    }



    @Override
    public void next() {
        super.next();
        Editable phone = signUpPhoneEditText.getText();
        Editable password = signUpPasswordEditText.getText();
        Editable code = signUpCodeEditText.getText();
        //TODO:send message to server

        //if success:reset password success;else return error message
        //success：
        context.finish();
    }
}
