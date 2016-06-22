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

import timber.log.Timber;

/**
 * @author: xiaoxiaoda
 * date: 16-1-22
 * email: daque@hustunique.com
 */
public class BaseSpotScrollView extends HorizontalScrollView {

  private float spotCircleRadius;
  private float spotItemLineWidth;
  private float spotItemLineHeight;
  private float spotTextSize;
  private int spotDoingColor;
  private int spotDoneColor;
  private int spotToDoColor;
  private int spotItemLineColor;

  static final float DEFAULT_SPOT_ITEM_LINE_WIDTH = 90.0f;
  static final float DEFAULT_SPOT_ITEM_LINE_HEIGHT = 10.0f;
  static final float DEFAULT_SPOT_CIRCLE_RADIUS = 50.0f;
  static final int DEFAULT_SPOT_DOING_COLOR = 0x769df6;
  static final int DEFAULT_SPOT_DONE_COLOR = 0xaa0000;
  static final int DEFAULT_SPOT_TO_DO_COLOR = 0x999999;
  static final int DEFAULT_SPOT_ITEM_LINE_COLOR = 0x769df6;

  protected boolean isClickable = true;

  protected List<SpotCircleView> spotList = new ArrayList<>();

  protected OnSpotItemClickListener mListener;

  protected LinearLayout mSpotList;

  protected SpotCircleView mCurrentCircle;

  protected LinearLayout.LayoutParams mSpotViewLayoutParams;

  public enum STATE {
    STATE_DONE(0),
    STATE_DOING(1),
    STATE_TO_DO(2);

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
          s = STATE_DONE;
          break;

        case 1:
          s = STATE_DOING;
          break;

        case 2:
          s = STATE_TO_DO;
          break;

        default:
          s = STATE_DONE;
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
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BaseSpotScrollView, defStyleAttr, 0);
    try {
      spotDoingColor = a.getColor(R.styleable.BaseSpotScrollView_spotDoingColor, DEFAULT_SPOT_DOING_COLOR);
      spotDoneColor = a.getColor(R.styleable.BaseSpotScrollView_spotDoneColor, DEFAULT_SPOT_DONE_COLOR);
      spotToDoColor = a.getColor(R.styleable.BaseSpotScrollView_spotToDoColor, DEFAULT_SPOT_TO_DO_COLOR);
      spotItemLineColor = a.getColor(R.styleable.BaseSpotScrollView_spotItemLineColor, DEFAULT_SPOT_ITEM_LINE_COLOR);
      spotItemLineHeight = a.getDimension(R.styleable.BaseSpotScrollView_spotItemLineHeight, DEFAULT_SPOT_ITEM_LINE_HEIGHT);
      spotItemLineWidth = a.getDimension(R.styleable.BaseSpotScrollView_spotItemLineWidth, DEFAULT_SPOT_ITEM_LINE_WIDTH);
      spotTextSize = a.getDimension(R.styleable.BaseSpotScrollView_spotTextSize, Display.sp2px(getContext(), 12));
      spotCircleRadius = a.getDimension(R.styleable.BaseSpotScrollView_spotCircleRadius, DEFAULT_SPOT_CIRCLE_RADIUS);
    } finally {
      a.recycle();
    }
    init(context);
  }

  private void init(Context context) {
    LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    mSpotList = new LinearLayout(context);
    mSpotList.setOrientation(LinearLayout.HORIZONTAL);
    addView(mSpotList, lp);
    mSpotViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    lp.gravity = Gravity.CENTER_VERTICAL;
  }

  protected void addSpot(int num, int count, STATE state) {
    SpotCircleView spotCircleView = new SpotCircleView(getContext());
    spotCircleView.setSpotDoingColor(spotDoingColor);
    spotCircleView.setSpotDoneColor(spotDoneColor);
    spotCircleView.setSpotToDoColor(spotToDoColor);
    spotCircleView.setSpotItemLineColor(spotItemLineColor);
    spotCircleView.setSpotItemLineHeight(spotItemLineHeight);
    spotCircleView.setSpotItemLineWidth(spotItemLineWidth);
    spotCircleView.setSpotTextSize(spotTextSize);
    spotCircleView.setmState(state);
    spotCircleView.setmRadius(spotCircleRadius);
    spotCircleView.setmText(String.valueOf(num + 1));

    if (num == 0) {
      spotCircleView.setFirstOne(true);
    }
    if (num == count - 1) {
      spotCircleView.setLastOne(true);
    }
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

    private int spotDoingColor;
    private int spotDoneColor;
    private int spotToDoColor;
    private int spotItemLineColor;

    private float mRadius;
    private float spotItemLineWidth;
    private float spotItemLineHeight;
    private float spotTextSize;

    private String mText;
    private STATE mState;

    private boolean isFirstOne = false;
    private boolean isLastOne = false;

    private int mTag;

    private float mHeight;
    private float mWidth;

    private Rect mBounds;
    private Paint mPaint;

    public SpotCircleView(Context context) {
      this(context, null);
    }

    public SpotCircleView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
    }

    public SpotCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
    }

    public int getSpotDoingColor() {
      return spotDoingColor;
    }

    public void setSpotDoingColor(int spotDoingColor) {
      this.spotDoingColor = spotDoingColor;
    }

    public int getSpotDoneColor() {
      return spotDoneColor;
    }

    public void setSpotDoneColor(int spotDoneColor) {
      this.spotDoneColor = spotDoneColor;
    }

    public int getSpotToDoColor() {
      return spotToDoColor;
    }

    public void setSpotToDoColor(int spotToDoColor) {
      this.spotToDoColor = spotToDoColor;
    }

    public int getSpotItemLineColor() {
      return spotItemLineColor;
    }

    public void setSpotItemLineColor(int spotItemLineColor) {
      this.spotItemLineColor = spotItemLineColor;
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

    public float getSpotItemLineWidth() {
      return spotItemLineWidth;
    }

    public void setSpotItemLineWidth(float spotItemLineWidth) {
      this.spotItemLineWidth = spotItemLineWidth;
    }

    public float getSpotItemLineHeight() {
      return spotItemLineHeight;
    }

    public void setSpotItemLineHeight(float spotItemLineHeight) {
      this.spotItemLineHeight = spotItemLineHeight;
    }

    public float getSpotTextSize() {
      return spotTextSize;
    }

    public void setSpotTextSize(float spotTextSize) {
      this.spotTextSize = spotTextSize;
    }

    public String getmText() {
      return mText;
    }

    public void setmText(String mText) {
      this.mText = mText;
      this.mTag = Integer.valueOf(mText);
      invalidate();
    }

    public boolean isFirstOne() {
      return isFirstOne;
    }

    public void setFirstOne(boolean firstOne) {
      isFirstOne = firstOne;
    }

    public boolean isLastOne() {
      return isLastOne;
    }

    public void setLastOne(boolean lastOne) {
      isLastOne = lastOne;
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
        mPaint.setTextSize(spotTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        float textWidth = mBounds.width();
        mWidth = (int) (getPaddingLeft() + textWidth + getPaddingRight() + spotItemLineWidth * 2);
      }

      if (heightMode == MeasureSpec.EXACTLY) {
        mHeight = heightSize;
      } else {
        mPaint.setTextSize(spotTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        float textHeight = mBounds.height();
        mHeight = (int) (getPaddingTop() + textHeight + getPaddingBottom());
      }
      int minRadius;
      if (mBounds.height() < mBounds.width()) {
        minRadius = mBounds.width() / 2;
      } else {
        minRadius = mBounds.height() / 2;
      }
      if (minRadius - 2 > mRadius) {
        mRadius = minRadius;
      }
      setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      drawLineBetweenItem(canvas);

      drawSpotBackground(canvas);

      drawSpotText(canvas);
    }

    private void drawLineBetweenItem(Canvas canvas) {
      mPaint.reset();
      mPaint.setAntiAlias(true);
      Rect rect;
      mPaint.setColor(spotItemLineColor);
      mPaint.setStrokeWidth(spotItemLineHeight);
      if (isFirstOne) {
        if (isLastOne) {
          rect = null;
        } else {
          rect = new Rect((int) mWidth / 2, (int) (mHeight - spotItemLineHeight) / 2, (int) mWidth, (int) (mHeight + spotItemLineHeight) / 2);
        }
      } else {
        if (isLastOne) {
          rect = new Rect(0, (int) (mHeight - spotItemLineHeight) / 2, (int) mWidth / 2, (int) (mHeight + spotItemLineHeight) / 2);
        } else {
          rect = new Rect(0, (int) (mHeight - spotItemLineHeight) / 2, (int) mWidth, (int) (mHeight + spotItemLineHeight) / 2);
        }
      }
      if (rect != null) {
        canvas.drawRect(rect, mPaint);
      }
    }

    private void drawSpotBackground(Canvas canvas) {
      mPaint.reset();
      mPaint.setAntiAlias(true);
      switch (mState) {
        case STATE_DONE:
          mPaint.setColor(getResources().getColor(R.color.default_font_color));
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius + Display.dip2px(3), mPaint);
          mPaint.setColor(spotDoneColor);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);
          break;

        case STATE_DOING:
          mPaint.setColor(getResources().getColor(R.color.default_font_color));
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius + Display.dip2px(3), mPaint);
          mPaint.setColor(spotDoingColor);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);
          break;

        case STATE_TO_DO:
          mPaint.setColor(spotToDoColor);
          canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mPaint);
          break;
      }
    }

    private void drawSpotText(Canvas canvas) {
      mPaint.reset();
      mPaint.setAntiAlias(true);
      mPaint.setTextSize(spotTextSize);
      mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
      if (mState == STATE.STATE_TO_DO) {
        mPaint.setColor(getResources().getColor(R.color.grey05));
      } else {
        mPaint.setColor(getResources().getColor(R.color.default_font_color));
      }

      canvas.drawText(mText, (mWidth - mPaint.measureText(mText)) / 2, mHeight / 2 + mBounds.height() / 2, mPaint);
    }

    @Override
    public void onClick(View v) {
      if (null == mListener || (!isClickable)) {
        return;
      }
      mListener.onSpotItemClick(v, mTag - 1);
    }

  }

}
