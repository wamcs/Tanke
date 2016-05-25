package com.lptiyu.tanke.initialization.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.initialization.ui.SignUpActivity;
import com.lptiyu.tanke.utils.ThirdLoginHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.LoginEditView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class LoginController extends ActivityController {



    @BindView(R.id.login_input_phone)
    LoginEditView mInputPhoneEditText;
    @BindView(R.id.login_input_password)
    LoginEditView mInputPasswordEditText;
    @BindView(R.id.login_qq_button)
    ImageView  mQqLogin;
    @BindView(R.id.login_weixin_button)
    ImageView mWeixinLogin;
    @BindView(R.id.login_weibo_button)
    ImageView mWeiboLogin;


    private boolean isButtonEnable=true;

    private ThirdLoginHelper helper;
    private long exitTime;

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

    @OnClick(R.id.login_forget_password_button)
    void forget(){
        Intent intent = new Intent(this.getContext(), SignUpActivity.class);
        intent.putExtra(Conf.SIGN_UP_CODE,Conf.RESET_PASSWORD_CODE);
        startActivity(intent);
    }

    @OnClick(R.id.login_sign_up_button)
    void signUp(){
        Intent intent = new Intent(this.getContext(), SignUpActivity.class);
        intent.putExtra(Conf.SIGN_UP_CODE,Conf.REGISTER_CODE);
        startActivity(intent);
    }

    @OnClick(R.id.login_login_button)
    void login(){


        if (mInputPhoneEditText.getText().length() == 0){
            ToastUtil.TextToast(getString(R.string.none_user_phone));
            return;
        }

        if (mInputPhoneEditText.getText().length() != 11){
            ToastUtil.TextToast(getString(R.string.error_user_phone));
            return;
        }

        if (mInputPasswordEditText.getText().length() == 0){
            ToastUtil.TextToast(getString(R.string.none_user_password));
            return;
        }

        String phoneNumber = mInputPhoneEditText.getText().toString();
        String password = mInputPasswordEditText.getText().toString();


        //TODO:send message to server

        //if success:

        Accounts.setPhoneNumber(phoneNumber);
        Accounts.setPassword(password);

        //TODO:jump to main activity


    }


    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ((System.currentTimeMillis() - exitTime)>2000){
            ToastUtil.TextToast(getString(R.string.exit));
            exitTime = System.currentTimeMillis();
        }else {
            System.exit(0);
        }
    }
}
