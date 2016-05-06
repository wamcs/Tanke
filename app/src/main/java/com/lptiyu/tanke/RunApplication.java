package com.lptiyu.tanke;

import android.app.Application;

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
    Timber.plant(new Timber.DebugTree());
  }
}
