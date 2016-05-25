package com.lptiyu.tanke.initialization.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.initialization.ui.CompleteInformationActivity;
import com.lptiyu.tanke.utils.ToastUtil;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class RegisterHelper extends SignUpHelper {

    public RegisterHelper(AppCompatActivity activity, View view) {
        super(activity, view);
    }

    @Override
    protected void init() {
        super.init();
        signUpTitle.setText(R.string.register_title);
        signUpNextButton.setText(R.string.next);
    }


    @Override
    public void next() {
        super.next();
        Editable phone = signUpPhoneEditText.getText();
        Editable password = signUpPasswordEditText.getText();
        Editable code = signUpCodeEditText.getText();
        //TODO:send message to server

        //if success:get userid and set it to account
        Intent intent =new Intent();
        intent.setClass(context, CompleteInformationActivity.class);
        context.startActivity(intent);
        context.finish();
    }


}
