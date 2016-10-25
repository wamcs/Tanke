package com.lptiyu.tanke.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/3/15
 * email:kaili@hustunique.com
 */
public class ScreenUtils {

  /**
   * make up screen shot = =
   * use so many bitmap
   *
   * @param toolbar
   * @param view
   * @param mapView
   * @param callBack
   */
  public static void captureScreen(final View toolbar, final View view, final TextureMapView mapView, final ScreenCallBack callBack) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        mapView.getMap().snapshot(new BaiduMap.SnapshotReadyCallback() {
          @Override
          public void onSnapshotReady(Bitmap bitmap) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            String fileName = "share_" + dateFormat.format(new Date()) + ".png";

            toolbar.setDrawingCacheEnabled(true);
            toolbar.buildDrawingCache();
            Bitmap toolbarBitmap = toolbar.getDrawingCache();
            Bitmap tb = Bitmap.createBitmap(toolbarBitmap, 0, 0, toolbarBitmap.getWidth(), toolbarBitmap.getHeight());
            toolbar.destroyDrawingCache();

            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap formBitmap = view.getDrawingCache();
            Bitmap fb = Bitmap.createBitmap(formBitmap, 0, 0, formBitmap.getWidth(), formBitmap.getHeight());
            view.destroyDrawingCache();

            if (null == fb || null == tb) {
              Timber.d("share bitmap not get");
              return;
            }
            try {
              Bitmap c = Bitmap.createBitmap(tb.getWidth(), tb.getHeight() + fb.getHeight(), Bitmap.Config.ARGB_4444);
              //draw data form on map screenshot
              Canvas canvas = new Canvas(c);
              canvas.drawBitmap(tb, 0, 0, null);
              canvas.drawBitmap(bitmap, 0, tb.getHeight(), null);
              canvas.drawBitmap(fb, 0, tb.getHeight(), null);
              canvas.save(Canvas.ALL_SAVE_FLAG);
              canvas.restore();
              File file = new File(DirUtils.getTempDirectory(), fileName);
              FileOutputStream outputStream = new FileOutputStream(file);
              c.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
              callBack.onScreenShot(file.getAbsolutePath());
              tb.recycle();
              fb.recycle();
              bitmap.recycle();
            } catch (FileNotFoundException e) {
              callBack.onScreenShot(null);
              tb.recycle();
              fb.recycle();
              bitmap.recycle();
              e.printStackTrace();
            }
          }
        });
      }
    }).start();
  }

  public interface ScreenCallBack {
    void onScreenShot(@Nullable String filePath);
  }
}
