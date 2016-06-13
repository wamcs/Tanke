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
    return Observable.just("http://www.chinaviki.com/photos/2713/b/1_2713_294535.jpg");
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
