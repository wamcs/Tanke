package com.lptiyu.tanke.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import timber.log.Timber;


/**
 * USER: xiaoxiaoda
 * DATE: 15-11-23
 * EMAIL: 2319821734@qq.com
 */
public class DirUtils {

  private static String SDCARD_ROOT_DIR;
  private static String DATA_ROOT_DIR;
  private static final String FILES = "/files";
  private static final String TEMP = "/temp";
  private static final String GAME = "/game";
  private static final String ANDROID_RESOURCE = "android.resource://";

  private static boolean hasSDCard() {
    String status = Environment.getExternalStorageState();
    return status.equals(Environment.MEDIA_MOUNTED);
  }

  public static void init(Context context) throws Exception {
    File file = context.getExternalFilesDir(null);
    if (file != null) {
      SDCARD_ROOT_DIR = file.getPath();
    } else {
      throw new Exception("GET SDCARD DIR ERROR");
    }
    DATA_ROOT_DIR = context.getFilesDir().toString();
  }

  public static File getDirectory(String rootDir, String type) {
    if (SDCARD_ROOT_DIR == null || DATA_ROOT_DIR == null) {
      Timber.e("you should invoke init() method before use DirUtils");
      return null;
    }
    File destDir = new File(rootDir + type);
    if (!destDir.exists()) {
      if (destDir.mkdirs()) {
        Timber.d("=======create dir======== %s", destDir.getAbsolutePath());
      } else {
        Timber.d("=======create dir========failed");
      }
    }
    return destDir;
  }

  public static File getTempDirectory() {
    return getDirectory(SDCARD_ROOT_DIR, TEMP);
  }

  public static File getGameDirectory() {
    return getDirectory(DATA_ROOT_DIR, GAME);
  }

}
