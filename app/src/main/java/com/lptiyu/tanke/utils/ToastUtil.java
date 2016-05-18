package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public class ToastUtil {

  private static Toast toast = null;

  public static int LENGTH_LONG = Toast.LENGTH_LONG;
  public static int LENGTH_SHORT = Toast.LENGTH_SHORT;
  private static Context CONTEXT = AppData.getContext();

  /**
   * show toast with text
   *
   * @param text
   */
  public static void TextToast(CharSequence text) {
    toast = Toast.makeText(CONTEXT, text, LENGTH_SHORT);
    toast.show();
  }

  /**
   * show toast with image
   *
   * @param bitmap
   * @param text
   */
  public static void ImageToast(Bitmap bitmap, CharSequence text) {
    ImageView img = new ImageView(CONTEXT);
    img.setImageBitmap(bitmap);
    ImageToast(img, text, LENGTH_LONG);
  }

  public static void ImageToast(int ImageResourceId, CharSequence text) {
    ImageView img = new ImageView(CONTEXT);
    img.setImageResource(ImageResourceId);
    ImageToast(img, text, LENGTH_LONG);
  }

  public static void ImageToast(int ImageResourceId, CharSequence text, int duration) {
    ImageView img = new ImageView(CONTEXT);
    img.setImageResource(ImageResourceId);
    ImageToast(img, text, duration);
  }

  public static void ImageToast(View view, CharSequence text) {
    ImageToast(view, text, LENGTH_LONG);
  }

  public static void ImageToast(View view, CharSequence text, int duration) {
    toast = Toast.makeText(CONTEXT, text, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    View toastView = toast.getView();
    LinearLayout ll = new LinearLayout(CONTEXT);
    ll.addView(view);
    ll.addView(toastView);
    toast.setView(ll);
    toast.show();
  }
}
