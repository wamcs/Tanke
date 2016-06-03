package com.lptiyu.tanke.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.lptiyu.tanke.global.AppData;

/**
 * author:wamcs
 * date:2016/5/20
 * email:kaili@hustunique.com
 */
public class Display {

  public static DisplayMetrics display() {
    if (AppData.getContext() == null) {
      return null;
    }
    return AppData.getContext().getResources().getDisplayMetrics();
  }

  public static int dip2px(float dp) {
    DisplayMetrics display = display();
    if (display == null) {
      return 0;
    }
    final float scale = display.density;
    return (int) (dp * scale + 0.5f);
  }

  public static float px2dip(int px) {
    DisplayMetrics display = display();
    if (display == null) {
      return 0;
    }
    return px / display.density;
  }

  /**
   * 将sp值转换为px值，保证文字大小不变
   *
   * @param spValue
   * @param fontScale （DisplayMetrics类中属性scaledDensity）
   * @return
   */
  public static int sp2px(Context context, float spValue) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spValue * fontScale + 0.5f);
  }
}
