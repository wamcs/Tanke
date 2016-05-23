package com.lptiyu.tanke.global;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public class AppData {

  private static Context sContext;

  private static int versionCode = -1;

  public static Context getContext() {
    return sContext;
  }

  public static int getVersionCode() {
    if (versionCode >= 0){
      return versionCode;
    }
    PackageManager manager = sContext.getPackageManager();
    PackageInfo info = null;
    try {
      info = manager.getPackageInfo(sContext.getPackageName(), 0);
      return versionCode = info.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return -1;
  }

  public static void init(Context context) {
    sContext = context.getApplicationContext();
  }

  public static String getPackageName() {
    return sContext.getPackageName();
  }

  public static File cacheDir() {
    return sContext.getCacheDir();
  }

  public static File cacheDir(String dir) {
    return new File(sContext.getCacheDir(), dir);
  }
}
