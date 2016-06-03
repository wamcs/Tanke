package com.lptiyu.tanke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;

/**
 * author:wamcs
 * date:2016/5/20
 * email:kaili@hustunique.com
 */
public class LoginEditView extends LinearLayout {

    private ImageView signImage;
    private EditText mEditText;

    private float mTextSize;
    private boolean isPassWord;
    private String mHintText;
    private float mHeight;
    private Drawable mImageDrawable;
    private int mBaseColor;
    private float mDistance;


    public LoginEditView(Context context) {
        super(context);
        init(null,0);
    }

    public LoginEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public LoginEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);


    }

    private void init(AttributeSet attrs,int defStyleAttr){
        prepare(attrs,defStyleAttr);
        signImage = new ImageView(getContext());
        mEditText = new EditText(getContext());
        //mBackgroundDrawable =generateBorder();
        configureParent(this);
        configureImageView(this,signImage);
        configureEditText(this,mEditText);

    }

    private void prepare(AttributeSet attrs,int defStyleAttr){
        if (attrs == null) {
            throw new IllegalStateException("Params must be defined in AttributeSet");
        }

        mBaseColor = getResources().getColor(R.color.white10);
        mTextSize = Display.dip2px(20);
        mHeight = 0;
        mImageDrawable = null;
        mDistance = 0;
        isPassWord = false;
        TypedArray ta;
        ta = getContext().obtainStyledAttributes(attrs,R.styleable.LoginEditView,defStyleAttr,0);
        if ( ta != null ){
            mBaseColor = ta.getColor(R.styleable.LoginEditView_takBaseColor,mBaseColor);
            mDistance = ta.getDimension(R.styleable.LoginEditView_takImageToText,mDistance);
            mHeight = ta.getDimension(R.styleable.LoginEditView_takInnerHeight,mHeight);
            mTextSize = ta.getDimension(R.styleable.LoginEditView_takTextSize,mTextSize);
            mImageDrawable = ta.getDrawable(R.styleable.LoginEditView_takSymbolDrawable);
            mHintText = ta.getString(R.styleable.LoginEditView_takHintText);
            isPassWord = ta.getBoolean(R.styleable.LoginEditView_takPassWordType,isPassWord);
            mHintText = ta.getString(R.styleable.LoginEditView_takHintText);
            ta.recycle();
        }

    }

    protected void configureParent(LinearLayout linearLayout) {
        linearLayout.setPadding(0, 0, 0, 0);
        linearLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_edit_bg));
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
    }

    protected void configureImageView(LinearLayout linearLayout, ImageView imageView) {
        imageView.setImageDrawable(mImageDrawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LayoutParams lp = new LayoutParams((int) mHeight, (int) mHeight);
        lp.rightMargin = (int) mDistance;
        lp.leftMargin = (int) mDistance;
        imageView.setBackgroundDrawable(null);
        linearLayout.addView(imageView, lp);
    }

    protected void configureEditText(LinearLayout linearLayout, EditText editText) {
        editText.setMaxLines(1);
        editText.setSingleLine();
        editText.setHintTextColor(mBaseColor);
        editText.setTextColor(mBaseColor);
        editText.setCursorVisible(false);
        editText.setHint(mHintText);
        editText.setBackgroundColor(getResources().getColor(R.color.white00));
        //only aim at this project
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        if (isPassWord) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        }
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.addView(editText, lp);

    }


    public Editable getText(){
        return mEditText.getText();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setHintText(String text) {
        mHintText = text;
        mEditText.setHint(text);
    }

    public void setImageDrawable(Drawable drawable) {
        mImageDrawable = drawable;
        signImage.setImageDrawable(drawable);
    }

}
