package com.lptiyu.tanke.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/15
 *
 * @author ldx
 */
public class CustomTextView extends TextView{

  private static Typeface typeface;

  public CustomTextView(Context context) {
    super(context);
    init();
  }

  public CustomTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
//    if (typeface == null) {
//      typeface = Typeface.createFromAsset(getContext().getAssets(), "DroidPingFang.ttf");
//    }
//    setTypeface(typeface);
  }

}
