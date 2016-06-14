package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;

import com.lptiyu.tanke.base.splash.BaseSplashActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;

import rx.Observable;

/**
 * author:wamcs
 * date:2016/5/22
 * email:kaili@hustunique.com
 */
public class SplashActivity extends BaseSplashActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected Observable<String> fetchSplashUrl() {
    return null;
  }

  @Override
  protected boolean isFirstInApp() {
    return AppData.isFirstInApp();
  }

  @Override
  protected boolean isAccountsValid() {
    return Accounts.isLogin();
  }


}
