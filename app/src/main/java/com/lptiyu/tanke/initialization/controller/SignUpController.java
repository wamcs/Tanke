package com.lptiyu.tanke.initialization.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.view.View;
import android.view.WindowManager;
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
import com.lptiyu.tanke.initialization.ui.UserProtocolActivity;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class SignUpController extends ActivityController {

    @BindView(R.id.sign_up_protocol_button)
    TextView mProtocolButton;

    private SignUpHelper signUpHelper;
    private String mProtocolURL;
    private boolean isReadProtocol;

    public SignUpController(AppCompatActivity activity, View view) {
        super(activity, view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this, view);
        init(activity, view);

    }

    private void init(AppCompatActivity activity, View view) {
        int code;
        code = getIntent().getIntExtra(Conf.SIGN_UP_CODE, 0);
        if (code == 0) {
            throw new IllegalStateException("code must be in Conf");
        }


        switch (code) {
            case Conf.REGISTER_CODE:
                int type = getIntent().getIntExtra(Conf.SIGN_UP_TYPE, -1);
                Timber.d("sign up type: %d",type);
                if (type == -1) {
                    throw new IllegalStateException("not has this type");
                }
                signUpHelper = new RegisterHelper(activity, view, type);
                HttpService.getUserService().userProtocol()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<Response<String>>() {
                            @Override
                            public void call(Response<String> stringResponse) {
                                int status = stringResponse.getStatus();
                                if (status != 1){
                                    ToastUtil.TextToast(stringResponse.getInfo());
                                    return;
                                }
                                mProtocolURL = stringResponse.getData();
                            }
                        });
                break;
            case Conf.RESET_PASSWORD_CODE:

                signUpHelper = new ResetPasswordHelper(activity, view);
                mProtocolButton.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Conf.PROTOCOL_CODE:
               isReadProtocol = data.getBooleanExtra(Conf.PROTOCOL_STATE,false);
                break;
        }
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }


    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        finish();
        return true;
    }

    @OnClick(R.id.sign_up_return_button)
    void back() {
        finish();
    }

    @OnClick(R.id.sign_up_get_code_button)
    void getCode() {
        signUpHelper.getCode();
    }

    @OnClick(R.id.sign_up_next_button)
    void next() {
        if (!isReadProtocol){
            openProtocol();
            return;
        }
        signUpHelper.next();
    }

    @OnClick(R.id.sign_up_protocol_button)
    void openProtocol(){
        Intent intent = new Intent(getContext(), UserProtocolActivity.class);
        intent.putExtra(Conf.PROTOCOL_URL,mProtocolURL);
        startActivityForResult(intent,Conf.PROTOCOL_CODE);

    }
}
