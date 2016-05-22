package com.lptiyu.tanke.initialization.controller;

import android.accounts.Account;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.initialization.ui.RegisterActivity;
import com.lptiyu.tanke.initialization.ui.ResetSecretActivity;
import com.lptiyu.tanke.utils.ShaPrefer;
import com.lptiyu.tanke.utils.ThirdLoginHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.LoginEditView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class LoginController extends ActivityController {



    @BindView(R.id.login_activity_input_phone)
    LoginEditView mInputPhoneEditText;
    @BindView(R.id.login_activity_input_secret)
    LoginEditView mInputSecretEditText;
    @BindView(R.id.login_activity_qq_button)
    ImageView  mQqLogin;
    @BindView(R.id.login_activity_weixin_button)
    ImageView mWeixinLogin;
    @BindView(R.id.login_activity_weibo_button)
    ImageView mWeiboLogin;

    private String phoneNumber;
    private String secret;


    private boolean isButtonEnable=true;

    private ThirdLoginHelper helper;

    public LoginController(AppCompatActivity activity, View view) {
        super(activity, view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this, view);
        init();
    }

    private void init(){
        helper=new ThirdLoginHelper();
        initClickEvent();

    }

    private void initClickEvent(){
        mQqLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isButtonEnable){
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP){
                    isButtonEnable = false;
                    mQqLogin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isButtonEnable = true;
                        }
                    },1000);
                    helper.oauthLogin(ThirdLoginHelper.QZONE);
                    mQqLogin.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mQqLogin.animate().scaleX(0.8f).scaleY(0.8f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                return true;
            }
        });

        mWeixinLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isButtonEnable){
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP){
                    isButtonEnable = false;
                    mWeixinLogin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isButtonEnable = true;
                        }
                    },1000);
                    helper.oauthLogin(ThirdLoginHelper.WECHAT);
                    mWeixinLogin.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mWeixinLogin.animate().scaleX(0.8f).scaleY(0.8f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                return true;
            }
        });

        mWeiboLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isButtonEnable){
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_UP){
                    isButtonEnable = false;
                    mWeiboLogin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isButtonEnable = true;
                        }
                    },1000);
                    helper.oauthLogin(ThirdLoginHelper.WEIBO);
                    mWeiboLogin.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    mWeiboLogin.animate().scaleX(0.8f).scaleY(0.8f).setInterpolator(new BounceInterpolator()).setDuration(100).start();
                    return true;
                }

                return true;
            }
        });
    }

    @OnClick(R.id.login_activity_forget_secret_button)
    void forget(){
        Intent intent = new Intent(this.getContext(), ResetSecretActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_activity_sign_up_button)
    void signUp(){
        Intent intent = new Intent(this.getContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_activity_login_button)
    void login(){


        if (mInputPhoneEditText.getText().length() == 0){
            ToastUtil.TextToast(getString(R.string.none_user_phone));
            return;
        }

        if (mInputPhoneEditText.getText().length() != 11){
            ToastUtil.TextToast(getString(R.string.error_user_phone));
            return;
        }

        if (mInputSecretEditText.getText().length() == 0){
            ToastUtil.TextToast(getString(R.string.none_user_password));
            return;
        }

        phoneNumber = mInputPhoneEditText.getText().toString();
        secret = mInputSecretEditText.getText().toString();


        //TODO:send message to server

        //if success:

        Accounts.setPhoneNumber(phoneNumber);
        Accounts.setSecret(secret);

        //TODO:jump to main activity


    }


    @Override
    protected boolean isToolbarEnable() {
        return false;
    }
}
