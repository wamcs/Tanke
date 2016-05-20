package com.lptiyu.tanke.base.controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.view.WindowManager;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/10/17
 *
 * @author ldx
 */
public abstract class ContextController extends BaseController {
  private Context mContext;

  public ContextController(Context context) {
    this.mContext = context;
  }

  public Context getContext() {
    return mContext;
  }

  public WindowManager getWindowManager() {
    return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
  }

  public AudioManager getAudioManager() {
    return (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
  }

  public String getString(int resId) {
    return mContext.getResources().getString(resId);
  }

  public Drawable getDrawable(int resId) {
    return mContext.getResources().getDrawable(resId);
  }

  public Resources getResources() {
    return mContext.getResources();
  }

}
