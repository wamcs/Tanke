package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.lptiyu.tanke.R;

public class URLDrawable extends BitmapDrawable {
    protected Drawable drawable;

    public URLDrawable(Context context) {
        drawable = ContextCompat.getDrawable(context, R.drawable.ic_launcher);
        drawable.setBounds(0, 0, getIntrinsicWidth(), getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }
}