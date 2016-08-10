package com.lptiyu.tanke.utils;

import com.google.gson.Gson;

/**
 * @author : xiaoxiaoda
 *         date: 16-1-27
 *         email: daque@hustunique.com
 */
public class ShaPreferManager {

  private static final String SETTING_MSG_PUSH = "setting_msg_push";
  private static final String SETTING_MOBILE_VIBRATE = "setting_mobile_vibrate";
  private static final String SETTING_SCREEN_LIGHT = "setting_screen_light";

  private static final String FIRST_IN_APP = "first_in_app";

  private static Gson gson = new Gson();

  public static boolean isFirstInApp() {
    if (ShaPrefer.getBoolean(FIRST_IN_APP, true)) {
      ShaPrefer.put(FIRST_IN_APP, false);
      return true;
    }
    return false;
  }

  public static void setMsgPush(boolean b) {
    ShaPrefer.put(SETTING_MSG_PUSH, b);
  }

  public static boolean getMsgPush() {
    return ShaPrefer.getBoolean(SETTING_MSG_PUSH, false);
  }

  public static void setMobileVibrate(boolean b) {
    ShaPrefer.put(SETTING_MOBILE_VIBRATE, b);
  }

  public static boolean getMobileVibrate() {
    return ShaPrefer.getBoolean(SETTING_MOBILE_VIBRATE, false);
  }

  public static void setScreenLight(boolean b) {
    ShaPrefer.put(SETTING_SCREEN_LIGHT, b);
  }

  public static boolean getScreenLight() {
    return ShaPrefer.getBoolean(SETTING_SCREEN_LIGHT, false);
  }

  public static <T> void putPojo(String key, T value) {
    ShaPrefer.put(key, gson.toJson(value));
  }

  public static <T> T getPojo(String key, Class<T> clazz) {
    return gson.fromJson(ShaPrefer.getString(key, ""), clazz);
  }
}
