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
  private static final String RES = "/res";
  private static final String RAW = "/raw";
  private static final String PHOTOS = "/photos";
  private static final String SHARE = "/share";
  private static final String ANDROID_RESOURCE = "android.resource://";

  private static boolean hasSDCard() {
    String status = Environment.getExternalStorageState();
    return status.equals(Environment.MEDIA_MOUNTED);
  }

  public static void init(Context context) {
    SDCARD_ROOT_DIR = context.getExternalFilesDir(null).getPath();
    DATA_ROOT_DIR = context.getFilesDir().toString();
  }

  public static File getDirectory(String s) {
    if (SDCARD_ROOT_DIR == null || DATA_ROOT_DIR == null) {
      Timber.e("you should invoke init() method before use DirUtils");
      return null;
    }
    StringBuilder stringBuilder = new StringBuilder();
    if (hasSDCard()) stringBuilder.append(SDCARD_ROOT_DIR).append(s);
    else stringBuilder.append(DATA_ROOT_DIR).append(FILES).append(s);
    File destDir = new File(stringBuilder.toString());
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
    return getDirectory(TEMP);
  }

  public static File getGameDirectory() {
    return getDirectory(GAME);
  }

  public static File getResDirectory() {
    return getDirectory(RES);
  }

  public static File getPhotosDirectory() {
    return getDirectory(PHOTOS);
  }

  public static File getSharePhotosDirectory() {
    return getDirectory(SHARE);
  }

}
