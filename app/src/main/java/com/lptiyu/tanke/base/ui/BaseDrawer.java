package com.lptiyu.tanke.base.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/10/16
 *
 * @author ldx
 */
public abstract class BaseDrawer {
  private View view;

  public BaseDrawer(View view) {
    this.view = view;
  }

  public View getView() {
    return view;
  }

  public Context getContext() {
    return view.getContext();
  }

  public int getWidth() {
    return view.getWidth();
  }

  public int getHeight() {
    return view.getHeight();
  }

  public int getMeasuredWidth() {
    return view.getMeasuredWidth();
  }

  public int getMeasuredHeight() {
    return view.getMeasuredHeight();
  }

  public Drawable loadDrawable(int resId) {
    return ContextCompat.getDrawable(getContext(), resId);
  }

  public abstract void draw(Canvas canvas);

}
