package com.lptiyu.tanke.global;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lptiyu.tanke.pojo.GameDisplayEntity;

import java.io.File;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public class AppData {

  private static Context sContext;

  private static int versionCode = -1;

  private static Gson sGson;

  static {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(GameDisplayEntity.GAME_STATE.class, GameDisplayEntity.GAME_STATE.NORMAL);
    builder.registerTypeAdapter(GameDisplayEntity.RECOMMENDED_TYPE.class, GameDisplayEntity.RECOMMENDED_TYPE.NORMAL);
    builder.registerTypeAdapter(GameDisplayEntity.GAME_TYPE.class, GameDisplayEntity.GAME_TYPE.INDIVIDUALS);
    sGson = builder.create();
  }

  public static Context getContext() {
    return sContext;
  }

  public static Gson globalGson() {
    return sGson;
  }

  public static int getVersionCode() {
    if (versionCode >= 0) {
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
