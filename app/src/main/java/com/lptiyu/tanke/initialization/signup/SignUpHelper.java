package com.lptiyu.tanke.initialization.signup;


import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class SignUpHelper {

    protected AppCompatActivity context;


    @BindView(R.id.sign_up_title)
    protected TextView signUpTitle;
    @BindView(R.id.sign_up_phone_edit_text)
    protected EditText signUpPhoneEditText;
    @BindView(R.id.sign_up_code_edit_text)
    protected EditText signUpCodeEditText;
    @BindView(R.id.sign_up_get_code_button)
    protected TextView signUpGetCodeButton;
    @BindView(R.id.sign_up_password_edit_text)
    protected EditText signUpPasswordEditText;
    @BindView(R.id.sign_up_next_button)
    protected Button signUpNextButton;

    protected final long COUNT_DOWN_TIME = 60000L;

    SignUpHelper(AppCompatActivity activity, View view){
        context = activity;
        ButterKnife.bind(this,view);
        init();
    }

    protected void init(){
        signUpNextButton.setClickable(false);
        signUpNextButton.setClickable(false);
        signUpNextButton.setEnabled(false);
    }


    public void getCode(){
        Editable phone = signUpPhoneEditText.getText();
        if (phone.length() < 0 ||phone.length() > 13){
            ToastUtil.TextToast(R.string.error_user_phone);
            return;
        }

    }


    public void next(){
        Editable phone = signUpPhoneEditText.getText();
        if (phone.length() < 0 ||phone.length() > 11){
            ToastUtil.TextToast(R.string.error_user_phone);
            return;
        }

        Editable password = signUpPasswordEditText.getText();
        if (password.length()<6){
            ToastUtil.TextToast(R.string.error_user_password);
            return;
        }



    }





    class TimeCounter extends CountDownTimer{

        public TimeCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            signUpGetCodeButton.setText(TimeUtils.getFriendlyTime(millisUntilFinished));
        }

        @Override
        public void onFinish() {
            signUpGetCodeButton.setText(R.string.get_verify_code);
            signUpGetCodeButton.setEnabled(true);
            signUpGetCodeButton.setClickable(true);
        }
    }



}
