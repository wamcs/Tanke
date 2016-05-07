package com.lptiyu.tanke.utils;

import android.content.Context;

/**
 * @author ldx
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
