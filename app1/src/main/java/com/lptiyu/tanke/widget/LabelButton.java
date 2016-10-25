package com.lptiyu.tanke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;


/**
 * author:wamcs
 * date:2015/12/26
 * email:kaili@hustunique.com
 */
public class LabelButton extends CustomTextView {

  public LabelButton(Context context) {
    super(context);
    init(context);
  }

  public LabelButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public LabelButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    int paddingHorizontol = Display.dip2px(8);
    int paddingVertial = Display.dip2px(8);
    int margin = Display.dip2px(10);
    setBackgroundResource(R.drawable.label_text_bg);
    setTextColor(context.getResources().getColor(R.color.white10));
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
    setPadding(paddingHorizontol, paddingVertial, paddingHorizontol, paddingVertial);
    layoutParams.setMargins(margin, margin, margin, margin);
    setLayoutParams(layoutParams);
    setTextSize(16);
  }

}
