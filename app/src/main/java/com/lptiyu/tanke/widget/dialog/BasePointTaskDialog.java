package com.lptiyu.tanke.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

/**
 * Created by Jason on 2016/8/3.
 */
public class BasePointTaskDialog extends Dialog {
    private NiftyDialogBuilder mBuilder;

    private Context mContext;

    public BasePointTaskDialog(Context context) {
        super(context);
        mContext = context;
        mBuilder = NiftyDialogBuilder.getInstance(context);
        mBuilder.withTitleColor(getColor(R.color.orange))
                .withDividerColor(getColor(R.color.transparent))
                .withDialogColor(getColor(R.color.default_font_color))
                // withDialogColor(int resid)
                .withDuration(300)
                .isCancelableOnTouchOutside(true)
                .withEffect(Effectstype.SlideBottom);
    }

    public BasePointTaskDialog withDividerColor(String colorString) {
        mBuilder.withDividerColor(colorString);
        return this;
    }

    public BasePointTaskDialog withButton1Text(String text) {
        mBuilder.withButton1Text(text);
        return this;
    }

    public BasePointTaskDialog setButton1Click(View.OnClickListener listener) {
        mBuilder.setButton1Click(listener);
        return this;
    }

    public BasePointTaskDialog withButton2Text(String text) {
        mBuilder.withButton2Text(text);
        return this;
    }

    public BasePointTaskDialog withDividerColor(int color) {
        mBuilder.withDividerColor(color);
        return this;
    }

    public BasePointTaskDialog withTitle(CharSequence title) {
        mBuilder.withTitle(title);
        return this;
    }

    public BasePointTaskDialog withTitleColor(String colorString) {
        mBuilder.withTitleColor(Color.parseColor(colorString));
        return this;
    }

    public BasePointTaskDialog withTitleColor(int color) {
        mBuilder.withTitleColor(color);
        return this;
    }

    public BasePointTaskDialog withMessage(int textResId) {
        mBuilder.withMessage(textResId);
        return this;
    }

    public BasePointTaskDialog withMessage(CharSequence msg) {
        mBuilder.withMessage(msg);
        return this;
    }

    public BasePointTaskDialog withMessageColor(String colorString) {
        mBuilder.withMessageColor(colorString);
        return this;
    }

    public BasePointTaskDialog withMessageColor(int color) {
        mBuilder.withMessageColor(color);
        return this;
    }

    public BasePointTaskDialog withDialogColor(String colorString) {
        mBuilder.withDialogColor(colorString);
        return this;
    }

    public BasePointTaskDialog withDialogColor(int color) {
        mBuilder.withDialogColor(color);
        return this;
    }

    public BasePointTaskDialog withIcon(int drawableResId) {
        mBuilder.withIcon(drawableResId);
        return this;
    }

    public BasePointTaskDialog withIcon(Drawable icon) {
        mBuilder.withIcon(icon);
        return this;
    }

    public BasePointTaskDialog withDuration(int duration) {
        mBuilder.withDuration(duration);
        return this;
    }

    public BasePointTaskDialog withEffect(Effectstype type) {
        mBuilder.withEffect(type);
        return this;
    }

    public BasePointTaskDialog isCancelableOnTouchOutside(boolean cancelable) {
        mBuilder.isCancelableOnTouchOutside(cancelable);
        return this;
    }

    public BasePointTaskDialog isCancelable(boolean cancelable) {
        mBuilder.isCancelable(cancelable);
        return this;
    }

    public BasePointTaskDialog setCustomView(int resId, Context context) {
        View v = Inflater.inflate(resId, null, false);
        return setCustomView(v, context);
    }

    public BasePointTaskDialog setCustomView(View view, Context context) {
        mBuilder.setCustomView(view, context);
        return this;
    }

    protected int getColor(int resId) {
        return getContext().getResources().getColor(resId);
    }

    @Override
    public void show() {
        mBuilder.show();
    }

    public void onDestory() {
        mBuilder = null;
    }

    @Override
    public void dismiss() {
        mBuilder.dismiss();
    }
}
