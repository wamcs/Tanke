package com.lptiyu.tanke;

import android.app.Activity;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.location.LocationFileParser;
import com.lptiyu.tanke.messagesystem.MessageActivity;
import com.lptiyu.tanke.utils.DirUtils;
import com.lptiyu.tanke.utils.thread;

import java.util.Stack;

import cn.sharesdk.framework.ShareSDK;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/5
 *
 * @author ldx
 */
public class RunApplication extends MultiDexApplication {

  private static Stack<Activity> activityStack;
  private static RunApplication singleton;

  @Override
  public void onCreate() {
    super.onCreate();
    singleton=this;

    MultiDex.install(this);

    Timber.plant(new Timber.DebugTree());
    AppData.init(this);

    try {
      ShareSDK.initSDK(this, "1276c2d783264");
      AVOSCloud.initialize(AppData.getContext(), "Wqseclbr8wx2kFAS7YseVc5n-gzGzoHsz", "1z4GofW1zaArBjcj53u3oBm1");
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

  // Returns the application instance
  public static RunApplication getInstance() {
    return singleton;
  }

  /**
   * add Activity 添加Activity到栈
   */
  public void addActivity(Activity activity){
    if(activityStack ==null){
      activityStack =new Stack<Activity>();
    }
    activityStack.add(activity);
  }
  /**
   * get current Activity 获取当前Activity（栈中最后一个压入的）
   */
  public Activity currentActivity() {
    Activity activity = activityStack.lastElement();
    return activity;
  }
  /**
   * 结束当前Activity（栈中最后一个压入的）
   */
  public void finishActivity() {
    Activity activity = activityStack.lastElement();
    finishActivity(activity);
  }

  /**
   * 结束指定的Activity
   */
  public void finishActivity(Activity activity) {
    if (activity != null) {
      activityStack.remove(activity);
      activity.finish();
      activity = null;
    }
  }

  /**
   * 结束指定类名的Activity
   */
  public void finishActivity(Class<?> cls) {
    for (Activity activity : activityStack) {
      if (activity.getClass().equals(cls)) {
        finishActivity(activity);
      }
    }
  }

  /**
   * 结束所有Activity
   */
  public void finishAllActivity() {
    for (int i = 0, size = activityStack.size(); i < size; i++) {
      if (null != activityStack.get(i)) {
        activityStack.get(i).finish();
      }
    }
    activityStack.clear();
  }

  /**
   * 退出应用程序
   */
  public void AppExit() {
    try {
      finishAllActivity();
    } catch (Exception e) {
    }
  }
}
