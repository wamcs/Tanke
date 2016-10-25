package com.lptiyu.tanke.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Jason on 2016/8/26.
 */
public class VibratorHelper {
    public static void startVibrator(Context context) {
        if (ShaPreferManager.getMobileVibrate()) {
            //震动提示
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }
    }
}
