package com.lptiyu.tanke;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.lptiyu.tanke.utils.AppData;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/5
 *
 * @author ldx
 */
public class RunApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    LeakCanary.install(this);
    SDKInitializer.initialize(this);
    Timber.plant(new Timber.DebugTree());
    AppData.init(this);
  }
}
