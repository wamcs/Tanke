package com.lptiyu.tanke.utils;

import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/13
 *
 * @author ldx
 */
public class Views {

  public static void setCursorDrawable(EditText editText, int resId) {
    try {
      Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
      f.setAccessible(true);
      f.set(editText, resId);
    } catch (Exception ignored) {
    }
  }
}
