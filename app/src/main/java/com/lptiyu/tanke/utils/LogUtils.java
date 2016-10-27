package com.lptiyu.tanke.utils;

import android.util.Log;

/**
 * Created by Jason on 2016/9/28.
 */

public class LogUtils {
    private static String TAG = "jason";

    public static void i(String text) {
        Log.i(TAG, text + "");
    }

    public static void i(String tag, String text) {
        Log.i(tag, text + "");
    }
}
