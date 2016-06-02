package com.lptiyu.tanke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaoxiaoda
 * date: 16-1-22
 * email: daque@hustunique.com
 */
public class BaseSpotScrollView extends HorizontalScrollView {

  protected boolean isClickable = true;

  protected List<SpotCircleView> spotList = new ArrayList<>();

  protected OnSpotItemClickListener mListener;

  protected LinearLayout mSpotList;

  protected SpotCircleView mCurrentCircle;

  protected LinearLayout.LayoutParams mSpotViewLayoutParams;

  private static final int DEFAULT_SOLIDE_COLOR = 0xFD3333;
  private static final float DEFAULT_STROKE_WIDTH = Display.dip2px(1);
  private static final float DEFAULT_TEXT_SIZE = 15;
  private static final int DEFAULT_STATE = STATE.STATE_STROKE_RED.getK();

  public enum STATE {
    STATE_STROKE_GREY(0),
    STATE_STROKE_RED(1),
    STATE_STROKE_GREEN(2),
    STATE_SOLID_GREY(3),
    STATE_SOLID_RED(4),
    STATE_SOLID_GREEN(5);

    private int k;

    STATE(int k) {
      this.k = k;
    }

    public int getK() {
      return k;
    }

    public static STATE fromValue(int i) {
      STATE s;
      switch (i) {
        case 0:
          s = STATE_STROKE_GREY;
          break;

        case 1:
          s = STATE_STROKE_RED;
          break;

        case 2:
          s = STATE_STROKE_GREEN;
          break;

        case 3:
          s = STATE_SOLID_GREY;
          break;

        case 4:
          s = STATE_SOLID_RED;
          break;

        case 5:
          s = STATE_SOLID_GREEN;
          break;
        default:
          s = STATE_STROKE_RED;
      }
      return s;
    }
  }

  public BaseSpotScrollView(Context context) {
    this(context, null);
  }

  public BaseSpotScrollView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseSpotScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mSpotList = new LinearLayout(context);
    mSpotList.setOrientation(LinearLayout.HORIZONTAL);
    mSpotList.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
    addView(mSpotList, lp);
    mSpotViewLayoutParams = new LinearLayout.LayoutParams(Display.dip2px(50), Display.dip2px(50));
    lp.gravity = Gravity.CENTER_VERTICAL;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

  }

  protected void addSpot(int num, STATE state) {
    SpotCircleView spotCircleView = new SpotCircleView(getContext());
    spotCircleView.setmSolidColor(getResources().getColor(R.color.colorPrimary));
    spotCircleView.setmState(state);
    spotCircleView.setmStrokeWidth(Display.dip2px(1));
    spotCircleView.setmText(String.valueOf(num));
    spotCircleView.setmTextSize(Display.sp2px(getContext(), 16));
    spotList.add(spotCircleView);
    mSpotList.addView(spotCircleView, mSpotViewLayoutParams);
  }

  public void setOnSpotItemClickListener(OnSpotItemClickListener listener) {
    mListener = listener;
  }

  public void setSpotClickable(boolean clickable) {
    isClickable = clickable;
  }

  public int getSpotCount() {
    if (null == spotList) {
      return 0;
    }
    return spotList.size();
  }

  public interface OnSpotItemClickListener {

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param view
     * @param position
     */
    void onSpotItemClick(View view, int position);
  }

  class SpotCircleView extends View implements OnClickListener {

    private float mRadius;
    private int mSolidColor;
    private float mStrokeWidth;
    private float mTextSize;
    private String mText;
    private STATE mState;

    private int mTag;

    private float mHeight;
    private float mWidth;

    private Paint mPaint;
    private Rect mBounds;

    public SpotCircleView(Context context) {
      this(context, null);
    }

    public SpotCircleView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
    }

    public SpotCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpotCircleView, defStyleAttr, 0);
      try {
        mSolidColor = a.getColor(R.styleable.SpotCircleView_spotSolidColor, DEFAULT_SOLIDE_COLOR);
        mStrokeWidth = a.getDimension(R.styleable.SpotCircleView_spotStrokeWidth, DEFAULT_STROKE_WIDTH);
        mTextSize = a.getDimension(R.styleable.SpotCircleView_spotTextSize, DEFAULT_TEXT_SIZE);
        mText = a.getString(R.styleable.SpotCircleView_spotText);
        mState = STATE.fromValue(a.getInteger(R.styleable.SpotCircleView_spotState, DEFAULT_STATE));
      } finally {
        a.recycle();
      }
      init();
    }

    public int getmTag() {
      return mTag;
    }

    public void setmTag(int mTag) {
      this.mTag = mTag;
      this.mText = String.valueOf(mTag);
    }

    public STATE getmState() {
      return mState;
    }

    public void setmState(STATE mState) {
      this.mState = mState;
    }

    public float getmRadius() {
      return mRadius;
    }

    public void setmRadius(float mRadius) {
      this.mRadius = mRadius;
    }

    public int getmSolidColor() {
      return mSolidColor;
    }

    public void setmSolidColor(int mSolidColor) {
      this.mSolidColor = mSolidColor;
    }

    public float getmStrokeWidth() {
      return mStrokeWidth;
    }

    public void setmStrokeWidth(float mStrokeWidth) {
      this.mStrokeWidth = mStrokeWidth;
    }

    public float getmTextSize() {
      return mTextSize;
    }

    public void setmTextSize(float mTextSize) {
      this.mTextSize = mTextSize;
    }

    public String getmText() {
      return mText;
    }

    public void setmText(String mText) {
      this.mText = mText;
      this.mTag = Integer.valueOf(mText);
      invalidate();
    }

    private void init() {
      mBounds = new Rect();
      mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      if (isClickable) {
        this.setOnClickListener(this);
      }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

      int widthMode = MeasureSpec.getMode(widthMeasureSpec);
      int widthSize = MeasureSpec.getSize(widthMeasureSpec);
      int heightMode = MeasureSpec.getMode(heightMeasureSpec);
      int heightSize = MeasureSpec.getSize(heightMeasureSpec);

      if (widthMode == MeasureSpec.EXACTLY) {
        mWidth = widthSize;
      } else {
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        float textWidth = mBounds.width();
        int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
        mWidth = desired;
      }

      if (heightMode == MeasureSpec.EXACTLY) {
        mHeight = heightSize;
      } else {
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        float textHeight = mBounds.height();
        int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
        mHeight = desired;
      }

      if (mWidth < mHeight) {
        mHeight = mWidth;
      } else {
        mWidth = mHeight;
      }
      mRadius = mHeight / 4;
      setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      canvas.drawColor(getResources().getColor(R.color.default_font_color));

      switch (mState) {

        case STATE_STROKE_GREY:
          mPaint.setColor(getResources().getColor(R.color.grey06));
          mPaint.setStyle(Paint.Style.STROKE);
          mPaint.setStrokeWidth(mStrokeWidth);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);

          mPaint.reset();
          mPaint.setAntiAlias(true);
          mPaint.setTextSize(mTextSize);
          mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
          mPaint.setColor(getResources().getColor(R.color.grey06));
          canvas.drawText(mText, (mWidth - mPaint.measureText(mText)) / 2, mHeight / 2 + mBounds.height() / 2, mPaint);
          break;

        case STATE_STROKE_RED:
          mPaint.setColor(getResources().getColor(R.color.colorPrimary));
          mPaint.setStyle(Paint.Style.STROKE);
          mPaint.setStrokeWidth(mStrokeWidth);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);

          mPaint.reset();
          mPaint.setAntiAlias(true);
          mPaint.setTextSize(mTextSize);
          mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
          mPaint.setColor(getResources().getColor(R.color.colorPrimary));
          canvas.drawText(mText, (mWidth - mPaint.measureText(mText)) / 2, mHeight / 2 + mBounds.height() / 2, mPaint);
          break;

        case STATE_STROKE_GREEN:
          mPaint.setColor(getResources().getColor(R.color.green));
          mPaint.setStyle(Paint.Style.STROKE);
          mPaint.setStrokeWidth(mStrokeWidth);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);

          mPaint.reset();
          mPaint.setAntiAlias(true);
          mPaint.setTextSize(mTextSize);
          mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
          mPaint.setColor(getResources().getColor(R.color.green));
          canvas.drawText(mText, (mWidth - mPaint.measureText(mText)) / 2, mHeight / 2 + mBounds.height() / 2, mPaint);
          break;

        case STATE_SOLID_GREY:
          mPaint.setColor(getResources().getColor(R.color.grey06));
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius + mStrokeWidth, mPaint);
          break;

        case STATE_SOLID_RED:
          mPaint.setColor(getResources().getColor(R.color.colorPrimary));
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius + mStrokeWidth, mPaint);
          break;

        case STATE_SOLID_GREEN:
          mPaint.setColor(getResources().getColor(R.color.green));
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius + mStrokeWidth, mPaint);
          break;
      }

      if (mState == STATE.STATE_SOLID_GREY || STATE.STATE_SOLID_RED == mState || STATE.STATE_SOLID_GREEN == mState) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        mPaint.setColor(getResources().getColor(R.color.default_font_color));
        canvas.drawText(mText, (mWidth - mPaint.measureText(mText)) / 2, mHeight / 2 + mBounds.height() / 2, mPaint);
      }
    }

    @Override
    public void onClick(View v) {
      if (null == mListener || (!isClickable)) {
        return;
      }
      mListener.onSpotItemClick(v, mTag);
    }
  }

}
