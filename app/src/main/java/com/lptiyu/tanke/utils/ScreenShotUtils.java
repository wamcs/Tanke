package com.lptiyu.tanke.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jason on 2016/11/7.
 */

public class ScreenShotUtils {
    public static String screenShot(Activity activity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String fname = DirUtils.getScreenShotDirectory().getAbsolutePath() + sdf.format(new Date()) + ".png";
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            LogUtils.i("bitmap got !");
            try {
                FileOutputStream out = new FileOutputStream(fname);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                LogUtils.i("分享截图完毕：" + fname);
                return fname;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.i("bitmap is null !");
        }
        return null;
    }

    public static String screenShot(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        String fname = DirUtils.getScreenShotDirectory().getAbsolutePath() + sdf.format(new Date()) + ".png";
        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            LogUtils.i("bitmap got !");
            try {
                FileOutputStream out = new FileOutputStream(fname);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                LogUtils.i("分享截图完毕：" + fname);
                bitmap.recycle();
                return fname;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.i("bitmap is null !");
        }
        return null;
    }
}
