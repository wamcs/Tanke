package com.lptiyu.tanke.initialization.signup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;

import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.initialization.ui.CompleteInformationActivity;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.io.net.UserService;
import com.lptiyu.tanke.pojo.UserEntity;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.rx.ToastExceptionAction;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class RegisterHelper extends SignUpHelper {

  private int type;

  public RegisterHelper(AppCompatActivity activity, View view, int type) {
    super(activity, view);
    this.type = type;
  }

  @Override
  protected void init() {
    super.init();
    signUpTitle.setText(R.string.register_title);
    signUpNextButton.setText(R.string.next);
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
        }, new ToastExceptionAction(signUpGetCodeButton.getContext().getApplicationContext()));
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

    if (type == UserService.USER_TYPE_NORMAL) {
      //普通用户注册
      HttpService.getUserService().register(phone, password, code, type)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new Action1<Response<UserEntity>>() {
            @Override
            public void call(Response<UserEntity> userEntityResponse) {
              int status = userEntityResponse.getStatus();
              if (status != 1) {
                ToastUtil.TextToast(userEntityResponse.getInfo());
                return;
              }
              UserEntity entity = userEntityResponse.getData();
              Accounts.setId(entity.getUid());
              Accounts.setToken(entity.getToken());
              //Accounts.setPhoneNumber(entity.getPhone());
              Intent intent = new Intent();
//              intent.setClass(context, MainActivity.class);
              intent.setClass(context, CompleteInformationActivity.class);
              context.startActivity(intent);
            }
          }, new ToastExceptionAction(context.getApplicationContext()));
    } else {
      //第三方用户注册
      HttpService.getUserService().registerThird(phone, password, code, type, Accounts.getOpenId(), Accounts.getNickName())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeOn(Schedulers.io())
          .subscribe(new Action1<Response<UserEntity>>() {
            @Override
            public void call(Response<UserEntity> userEntityResponse) {
              int status = userEntityResponse.getStatus();
              if (status != 1) {
                ToastUtil.TextToast(userEntityResponse.getInfo());
                return;
              }
              UserEntity entity = userEntityResponse.getData();
              Accounts.setId(entity.getUid());
              Accounts.setToken(entity.getToken());
              //Accounts.setPhoneNumber(entity.getPhone());
              Intent intent = new Intent();
//              intent.setClass(context, MainActivity.class);
              intent.setClass(context, CompleteInformationActivity.class);
              context.startActivity(intent);
            }
          }, new ToastExceptionAction(context.getApplicationContext()));
    }
    return true;
  }
}
