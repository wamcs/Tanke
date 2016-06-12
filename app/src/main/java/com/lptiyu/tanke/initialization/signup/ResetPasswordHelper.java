package com.lptiyu.tanke.initialization.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.ToastUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
    public void getCode() {
        super.getCode();

        String phone = signUpPhoneEditText.getText().toString();
        HttpService.getUserService().getVerifyCode(2, phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        int status = voidResponse.getStatus();
                        if (status != 1) {
                            ToastUtil.TextToast(voidResponse.getInfo());
                            return;
                        }
                        signUpGetCodeButton.setEnabled(false);
                        signUpGetCodeButton.setClickable(false);
                        TimeCounter timeCounter = new TimeCounter(COUNT_DOWN_TIME, 1000);
                        timeCounter.start();
                        signUpNextButton.setClickable(true);
                        signUpNextButton.setEnabled(true);
                    }
                });


    }

    @Override
    public void next() {
        super.next();
        String phone = signUpPhoneEditText.getText().toString();
        String password = signUpPasswordEditText.getText().toString();
        String code = signUpCodeEditText.getText().toString();

        HttpService.getUserService().forgetPassword(phone, password, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> voidResponse) {
                        int status = voidResponse.getStatus();
                        if (status != 1) {
                            ToastUtil.TextToast(voidResponse.getInfo());
                            return;
                        }
                      ToastUtil.TextToast(voidResponse.getInfo());
                        context.finish();
                    }
                });
    }
}
