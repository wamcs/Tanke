package com.lptiyu.tanke.activities.initialization.signup;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Toast;

import com.lptiyu.tanke.enums.ResultCode;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.net.UserService;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;
import com.lptiyu.tanke.entity.response.BaseResponse;

import org.xutils.http.RequestParams;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class BindTelHelper extends SignUpHelper {

    public BindTelHelper(AppCompatActivity activity, View view) {
        super(activity, view);
        init();
    }

    protected void init() {
        signUpTitle.setText("绑定手机号");
        //        signUpNextButton.setText(R.string.next);
    }

    @Override
    public boolean getCode() {
        if (!super.getCode()) {
            return false;
        }

        Editable phone = signUpPhoneEditText.getText();
        HttpService.getUserService().getVerifyCode(UserService.REGISTER, phone.toString())
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
        final String phone = signUpPhoneEditText.getText().toString();
        String password = signUpPasswordEditText.getText().toString();
        String code = signUpCodeEditText.getText().toString();
        signUpNextButton.setEnabled(false);
        signUpNextButton.setText("绑定中...");

        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.BIND_PHONE);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("pwd", password);
        params.addBodyParameter("uid", Accounts.getId() + "");
        params.addBodyParameter("openid", Accounts.getOpenId());
        params.addBodyParameter("code", code);
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<BaseResponse>() {
            @Override
            protected void onSuccess(BaseResponse baseResponse) {
                if (baseResponse.status == Response.RESPONSE_OK) {
                    Accounts.setPhoneNumber(phone);
                    context.setResult(ResultCode.BIND_TEL);
                    context.finish();
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                ToastUtil.TextToast("绑定失败");
                signUpNextButton.setEnabled(true);
                signUpNextButton.setText("完成");
            }
        }, BaseResponse.class);


        return true;
    }
}
