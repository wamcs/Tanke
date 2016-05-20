package com.lptiyu.tanke.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * author:wamcs
 * date:2016/5/20
 * email:kaili@hustunique.com
 */
public class LoginEditView extends View {

    private ImageView signImage;
    private EditText mEditText;

    private float mTextSize;
    private boolean isPassWord;
    private String mHintText;
    private float mHeight;
    private Drawable mImageDrawable;


    public LoginEditView(Context context) {
        super(context);
    }

    public LoginEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){

    }
}
