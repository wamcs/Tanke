package com.lptiyu.tanke.global;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.pojo.GAME_STATE;
import com.lptiyu.tanke.pojo.GAME_TYPE;
import com.lptiyu.tanke.pojo.RECOMMENDED_TYPE;
import com.lptiyu.tanke.pojo.Team;
import com.lptiyu.tanke.utils.ShaPrefer;

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
    builder.registerTypeAdapter(GAME_STATE.class, GAME_STATE.NORMAL);
    builder.registerTypeAdapter(RECOMMENDED_TYPE.class, RECOMMENDED_TYPE.NORMAL);
    builder.registerTypeAdapter(GAME_TYPE.class, GAME_TYPE.INDIVIDUALS);
    builder.registerTypeAdapter(Team.UserStatus.class, Team.UserStatus.MASTER);
    builder.registerTypeAdapter(Task.TASK_TYPE.class, Task.TASK_TYPE.FINISH);
    builder.registerTypeAdapter(RunningRecord.RECORD_TYPE.class, RunningRecord.RECORD_TYPE.GAME_START);
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

  public static boolean isFirstInApp() {
    // 如果获取不到这个键，就返回true, 则判断为第一次进入这个App
    if (ShaPrefer.getBoolean("firstInApp", true)) {
      // 现在把该值变成false, 即，以后进入不是第一次进入
      ShaPrefer.put("firstInApp", false);
      return true;
    } else {
      return false;
    }
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
