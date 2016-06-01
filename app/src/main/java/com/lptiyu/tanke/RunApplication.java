package com.lptiyu.tanke;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.utils.DirUtils;

import cn.sharesdk.framework.ShareSDK;
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
    AppData.init(this);
    try {
      ShareSDK.initSDK(this, "1276c2d783264");
      SDKInitializer.initialize(this);
      DirUtils.init(this);
      Fresco.initialize(this);

    } catch (Exception e) {
      // To test it automatically.
      Timber.e(e, e.getMessage());
    }
  }
}
