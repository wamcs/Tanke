package com.lptiyu.tanke.initialization.controller;

import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.initialization.signup.RegisterHelper;
import com.lptiyu.tanke.initialization.signup.ResetPasswordHelper;
import com.lptiyu.tanke.initialization.signup.SignUpHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class SignUpController extends ActivityController {

    private SignUpHelper signUpHelper;

    public SignUpController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this,view);
        init(activity,view);

    }

    private void init(AppCompatActivity activity, View view){
        int code;
        code = getIntent().getIntExtra(Conf.SIGN_UP_CODE,0);
        if (code == 0){
            throw new IllegalStateException("code must be in Conf");
        }


        switch (code){
            case Conf.REGISTER_CODE:
            signUpHelper = new RegisterHelper(activity,view);
                break;
            case Conf.RESET_PASSWORD_CODE:
                signUpHelper = new ResetPasswordHelper(activity,view);
                break;
        }

    }


    @Override
    protected boolean isToolbarEnable() {
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.sign_up_return_button)
    void back(){
        finish();
    }

    @OnClick(R.id.sign_up_get_code_button)
    void getCode(){
        signUpHelper.getCode();
    }

    @OnClick(R.id.sign_up_next_button)
    void next(){
        signUpHelper.next();
    }
}
