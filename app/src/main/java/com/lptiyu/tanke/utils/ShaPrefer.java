package com.lptiyu.tanke.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lptiyu.tanke.global.AppData;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/10/24
 *
 * @author ldx
 */
public class ShaPrefer {

  private ShaPrefer() {

  }

  private static final String DEFAULT_SHAPRE_NAME = "setting";

  private static SharedPreferences preferences = null;

  public static String getString(String key, String defValue) {
    checkDefault();
    return preferences.getString(key, defValue);
  }

  public static boolean getBoolean(String key, boolean defValue) {
    checkDefault();
    return preferences.getBoolean(key, defValue);
  }

  public static float getFloat(String key, float defValue) {
    checkDefault();
    return preferences.getFloat(key, defValue);
  }

  public static int getInt(String key, int defValue) {
    checkDefault();
    return preferences.getInt(key, defValue);
  }

  public static long getLong(String key,long defValue){
    checkDefault();
    return preferences.getLong(key,defValue);
  }

  public static void remove(String...keys){
    checkDefault();
    SharedPreferences.Editor editor = preferences.edit();
    for (String key : keys){
      editor.remove(key);
    }
    editor.apply();
  }

  public static void put(String key, Object value) {
    checkDefault();
    SharedPreferences.Editor editor = preferences.edit();
    if (value instanceof Boolean) {
      editor.putBoolean(key, (Boolean) value);
    } else if (value instanceof Integer) {
      editor.putInt(key, (Integer) value);
    } else if (value instanceof Long) {
      editor.putLong(key, (Long) value);
    } else if (value instanceof Float) {
      editor.putFloat(key, (Float) value);
    } else if (value instanceof String) {
      editor.putString(key, (String) value);
    } else {
      throw new IllegalArgumentException("ShaPrefer illegal argument");
    }
    editor.apply();
  }


  private static void checkDefault() {
    if (preferences == null) {
      preferences = AppData.getContext().getSharedPreferences(DEFAULT_SHAPRE_NAME, Context.MODE_PRIVATE);
    }
  }
}
