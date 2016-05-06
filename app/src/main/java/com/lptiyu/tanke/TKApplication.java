package com.lptiyu.tanke;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-5
 *         email: daque@hustunique.com
 */
public class TKApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    SDKInitializer.initialize(getApplicationContext());
  }

}
