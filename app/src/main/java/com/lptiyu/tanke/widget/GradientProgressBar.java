package com.lptiyu.tanke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.lptiyu.tanke.R;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/4
 *
 * @author ldx
 */
public class GradientProgressBar extends View {

  private Drawable gradientDrawable;

  private float progress;

  public GradientProgressBar(Context context) {
    this(context, null);
  }

  public GradientProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientProgressBar, 0, 0);
    gradientDrawable = a.getDrawable(R.styleable.GradientProgressBar_src);
    a.recycle();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (gradientDrawable == null) {
      return;
    }
    canvas.clipRect(0, 0, progress * getWidth(), getHeight());
    gradientDrawable.setBounds(0, 0, getWidth(), getHeight());
    gradientDrawable.draw(canvas);
  }

  public Drawable getGradientDrawable() {
    return gradientDrawable;
  }

  public void setGradientDrawable(Drawable gradientDrawable) {
    this.gradientDrawable = gradientDrawable;
  }

  public float getProgress() {
    return progress;
  }

  public void setProgress(float progress) {
    this.progress = progress;
    invalidate();
  }
}
