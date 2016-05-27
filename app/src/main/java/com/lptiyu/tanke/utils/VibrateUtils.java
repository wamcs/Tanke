package com.lptiyu.tanke.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.lptiyu.tanke.global.AppData;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/1/26
 *
 * @author ldx
 */
public class VibrateUtils {

  private VibrateUtils() {
  }

  public static Vibrator vibrator;

  public static boolean enable = true;

  private static void init() {
    //获取系统的Vibrator服务
    Context c = AppData.getContext();
    vibrator = (Vibrator) c.getSystemService(Service.VIBRATOR_SERVICE);
  }

  public static void vibrate() {
    if (!enable) {
      return;
    }

    if (vibrator == null) {
      init();
    }
    enable = false;
    vibrator.vibrate(new long[]{300, 300, 300}, 0);
  }

  public static void cancel() {
    if (null == vibrator) {
      return;
    }
    vibrator.cancel();
    enable = true;
  }
}
