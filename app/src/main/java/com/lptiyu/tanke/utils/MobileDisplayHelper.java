package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MobileDisplayHelper {
    private static WindowManager windowManager;

    private static void getWindowManager(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /*
    *   当前手机的宽：1080
        当前手机的高：1920
        DisplayMetrics:DisplayMetrics{density=3.0, width=1080, height=1920, scaledDensity=3.0, xdpi=480.0, ydpi=480.0}
        desity:3.0
        desityDpi:480.0
        scaleDesity:2.82
        */

    /**
     * 获取当前手机的分辨率 width = size.x; height = size.y;
     *
     * @param context
     * @return
     */
    public static Point getMobileWidthHeight(Context context) {
        getWindowManager(context);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static DisplayMetrics getMobileDisplayMetrics(Context context) {
        getWindowManager(context);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static float getMobileDesity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getMobileDesityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static float getMobileScaleDesity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    private static void measureView(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }

    public static int getViewHeight(View view) {
        measureView(view);
        return view.getMeasuredHeight();
    }

    public static int getViewWidth(View view) {
        measureView(view);
        return view.getMeasuredWidth();
    }
}
