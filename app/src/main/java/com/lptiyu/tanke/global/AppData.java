package com.lptiyu.tanke.global;

import android.content.Context;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public class AppData {

  private static Context sContext;

  public static Context getContext() {
    return sContext;
  }

  public static void init(Context context) {
    sContext = context.getApplicationContext();
  }

  public static String getPackageName(){
    return sContext.getPackageName();
  }
}
