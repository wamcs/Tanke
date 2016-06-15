package com.lptiyu.tanke;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.location.LocationFileParser;
import com.lptiyu.tanke.messagesystem.MessageActivity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.thread;

import cn.sharesdk.framework.ShareSDK;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/5
 *
 * @author ldx
 */
public class RunApplication extends MultiDexApplication {

  @Override
  public void onCreate() {
    super.onCreate();

    MultiDex.install(this);

    Timber.plant(new Timber.DebugTree());
    AppData.init(this);

    try {
      ShareSDK.initSDK(this, "1276c2d783264");
      AVOSCloud.initialize(AppData.getContext(),"Wqseclbr8wx2kFAS7YseVc5n-gzGzoHsz","1z4GofW1zaArBjcj53u3oBm1");
      PushService.setDefaultPushCallback(this, MessageActivity.class);
      SDKInitializer.initialize(this);
      DirUtils.init(this);
      Fresco.initialize(this);
      if (Accounts.getInstallationId().isEmpty()) {
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
        Accounts.setInstallationId(installationId);
        Timber.d("this device installation is %s", installationId);
      }

    } catch (Exception e) {
      // To test it automatically.
      Timber.e(e, e.getMessage());
    }

    thread.background(new Runnable() {
      @Override
      public void run() {
        readLocationFile();
      }
    });
  }

  private void readLocationFile() {
    if (LocationFileParser.init(getApplicationContext(), LocationFileParser.FILE_TYPE_FROM_DIR, Conf.DEFAULT_CITY_FILE_NAME)) {
      Timber.d("Loading Location file from Dir success");
    } else {
      Timber.d("Start Loading Default Assert");
      LocationFileParser.init(getApplicationContext(), LocationFileParser.FILE_TYPE_FROM_ASSETS, Conf.DEFAULT_CITY_FILE_NAME);
    }
  }
}
