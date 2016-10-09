package com.lptiyu.tanke.initialization.signup;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.net.UserService;
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
        init();
    }

    protected void init() {

        signUpTitle.setText(R.string.reset_password_title);
        //    signUpNextButton.setText(R.string.ensure);
    }


    @Override
    public boolean getCode() {
        if (!super.getCode()) {
            return false;
        }

        String phone = signUpPhoneEditText.getText().toString();
        HttpService.getUserService().getVerifyCode(UserService.RESET_PSW, phone)
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
                        signUpNextButton.setEnabled(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return true;
    }

    @Override
    public boolean next() {
        if (!super.next()) {
            return false;
        }
        String phone = signUpPhoneEditText.getText().toString();
        String password = signUpPasswordEditText.getText().toString();
        String code = signUpCodeEditText.getText().toString();
        signUpNextButton.setEnabled(false);
        signUpNextButton.setText("重置中...");

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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.TextToast("提交失败");
                        signUpNextButton.setEnabled(true);
                        signUpNextButton.setText("完成");
                    }
                });
        return true;
    }
}
