package com.lptiyu.tanke.activities.initialization.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.activities.initialization.signup.BindTelHelper;
import com.lptiyu.tanke.activities.initialization.signup.RegisterHelper;
import com.lptiyu.tanke.activities.initialization.signup.ResetPasswordHelper;
import com.lptiyu.tanke.activities.initialization.signup.SignUpHelper;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class SignUpActivity extends MyBaseActivity {
    @BindView(R.id.sign_up_protocol_button)
    TextView mProtocolButton;
    @BindView(R.id.sign_up_protocol_button_tip)
    TextView mProtocolButtonTip;
    @BindView(R.id.sign_up_next_button)
    TextView mBtnFinish;
    @BindView(R.id.default_tool_bar_textview)
    CustomTextView defaultToolBarTextview;

    private SignUpHelper signUpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        defaultToolBarTextview.setText("手机号注册");
        int code;
        code = getIntent().getIntExtra(Conf.SIGN_UP_CODE, 0);
        if (code == 0) {
            throw new IllegalStateException("code must be in Conf");
        }


        switch (code) {
            case Conf.REGISTER_CODE:
                int type = getIntent().getIntExtra(Conf.SIGN_UP_TYPE, -1);
                Timber.d("sign up type: %d", type);
                if (type == -1) {
                    throw new IllegalStateException("not has this type");
                }
                signUpHelper = new RegisterHelper(this, getWindow().getDecorView(), type);
                break;
            case Conf.RESET_PASSWORD_CODE:
                signUpHelper = new ResetPasswordHelper(this, getWindow().getDecorView());
                mProtocolButton.setVisibility(View.GONE);
                mProtocolButtonTip.setVisibility(View.GONE);
                break;
            case Conf.BIND_TEL:
                signUpHelper = new BindTelHelper(this, getWindow().getDecorView());
                mProtocolButton.setVisibility(View.GONE);
                mProtocolButtonTip.setVisibility(View.GONE);
                break;
        }

    }

    @OnClick(R.id.sign_up_get_code_button)
    void getCode() {
        signUpHelper.getCode();
    }

    @OnClick(R.id.sign_up_next_button)
    void next() {
        signUpHelper.next();
    }

    @OnClick(R.id.sign_up_protocol_button)
    void openProtocol() {
        Intent intent = new Intent(SignUpActivity.this, UserProtocolActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.default_tool_bar_imageview)
    public void onClick() {
        finish();
    }
}
