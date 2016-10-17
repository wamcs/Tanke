package com.lptiyu.tanke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.DisplayUtils;

import java.util.Date;


/**
 * @author : xiaoxiaoda
 *         date: 16-6-4
 *         email: wonderfulifeel@gmail.com
 */
public class TickView extends View {

  private int tickTextColor;
  private int tickInnerCircleColor;
  private int tickOuterCircleColor;
  private int tickFillColor;

  private int tickTextSize;
  private float tickInnerCircleRadius;
  private float tickOuterCircleRadius;

  private float mHeight;
  private float mWidth;
  private Point mCenter;

  private Rect mTextRect;
  private RectF mArc;

  private Paint mOuterPaint;
  private Paint mInnerPaint;
  private Paint mArcPaint;
  private Paint mTextPaint;

  private String tickTextFormatter;
  private CountDownTimer mTimer;

  private boolean isTicking = false;
  private boolean isHeartBeating = false;
  private long mMillisInFuture;
  private long mMillisRemianing;

  private OnTickFinishListener mListener;

  private static final long TEN_SECONDS_MILLIS = 10000L;

  private static final int DEFAULT_TEXT_COLOR = 0xffffff;
  private static final int DEFAULT_INNER_CIRCLE_COLOR = 0x769df6;
  private static final int DEFAULT_OUTER_CIRCLE_COLOR = 0xffffff;
  private static final int DEFAULT_FILL_COLOR = 0x40b553;
  private static final int DEFAULT_TEXT_SIZE = 80;
  private static final float DEFAULT_INNER_CIRCLE_RADIUS = DisplayUtils.dp2px(30);
  private static final float DEFAULT_OUTER_CIRCLE_RADIUS = DisplayUtils.dp2px(40);
  private static final float DEFAULT_FILL_WIDTH = DisplayUtils.dp2px(5);
  private static final float DEFAULT_START_ANGLE = 270.0f;
  private static final long DEFAULT_TICK_INTERVAL = 16L;

  public TickView(Context context) {
    this(context, null);
  }

  public TickView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TickView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TickView, defStyleAttr, 0);
    try {
      tickTextColor = a.getColor(R.styleable.TickView_tickTextColor, DEFAULT_TEXT_COLOR);
      tickInnerCircleColor = a.getColor(R.styleable.TickView_tickInnerCircleColor, DEFAULT_INNER_CIRCLE_COLOR);
      tickOuterCircleColor = a.getColor(R.styleable.TickView_tickOuterCircleColor, DEFAULT_OUTER_CIRCLE_COLOR);
      tickFillColor = a.getColor(R.styleable.TickView_tickFillColor, DEFAULT_FILL_COLOR);
      tickTextSize = a.getDimensionPixelSize(R.styleable.TickView_tickTextSize, DEFAULT_TEXT_SIZE);
      tickInnerCircleRadius = a.getDimension(R.styleable.TickView_tickInnerCircleRadius, DEFAULT_INNER_CIRCLE_RADIUS);
      tickOuterCircleRadius = a.getDimension(R.styleable.TickView_tickOuterCircleRadius, DEFAULT_OUTER_CIRCLE_RADIUS);
    } finally {
      a.recycle();
    }
    init();
  }

  private void init() {
    mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mOuterPaint.setColor(tickOuterCircleColor);

    mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mInnerPaint.setColor(tickInnerCircleColor);

    mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mArcPaint.setColor(tickFillColor);
    mArcPaint.setStrokeWidth(tickOuterCircleRadius - tickInnerCircleRadius);

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setColor(tickTextColor);
    mTextPaint.setTextSize(tickTextSize);
    mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

    mTextRect = new Rect();
    mArc = new RectF();
    mCenter = new Point();
    tickTextFormatter = getContext().getString(R.string.tick_view_message_formatter);

    fitText(tickTextFormatter);
    calculateEverything();
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
      mTextPaint.setTextSize(tickTextSize);
      mTextPaint.getTextBounds(tickTextFormatter, 0, tickTextFormatter.length(), mTextRect);
      mWidth = tickOuterCircleRadius * 2 + getPaddingLeft() + getPaddingRight();
    }
    if (heightMode == MeasureSpec.EXACTLY) {
      mHeight = heightSize;
    }
    mHeight = Math.max(mWidth, mHeight);
    mWidth = mHeight;
    setMeasuredDimension((int) mWidth, (int) mHeight);
    calculateEverything();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    if (changed) {
      calculateEverything();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    calculateArc();
    drawOuterCircle(canvas);
    drawArc(canvas);
    drawInnerCircle(canvas);
    drawText(canvas);
  }

  private void fitText(CharSequence text) {
    if (TextUtils.isEmpty(text)) {
      return;
    }
    float textWidth = mArcPaint.measureText(text.toString());
    float multi = (tickOuterCircleRadius * 2) / textWidth;
    mTextPaint.setTextSize(mArcPaint.getTextSize() * multi);
    mTextPaint.getTextBounds(text.toString(), 0, text.length(), mTextRect);
  }

  private void calculateEverything() {
    calculateCenter();
    calculateArc();
  }

  private void calculateCenter() {
    mCenter.x = (int) (mWidth) / 2;
    mCenter.y = (int) (mHeight) / 2;
  }

  private void calculateArc() {
    mArc.left = mCenter.x - tickOuterCircleRadius;
    mArc.top = mCenter.y - tickOuterCircleRadius;
    mArc.right = mCenter.x + tickOuterCircleRadius;
    mArc.bottom = mCenter.y + tickOuterCircleRadius;
  }

  private void drawOuterCircle(Canvas canvas) {
    canvas.drawCircle(mCenter.x, mCenter.y, tickOuterCircleRadius, mOuterPaint);
  }

  private void drawInnerCircle(Canvas canvas) {
    canvas.drawCircle(mCenter.x, mCenter.y, tickInnerCircleRadius, mInnerPaint);
  }

  private void drawText(Canvas canvas) {
    Date date = new Date(mMillisRemianing);
    String content = String.format(tickTextFormatter, date, date);
    canvas.drawText(content, (mWidth - mTextPaint.measureText(content)) / 2, mHeight / 2 + mTextRect.height() / 2, mTextPaint);
  }

  private void drawArc(Canvas canvas) {
    float percentage = (((float) mMillisRemianing) / ((float) mMillisInFuture));
    float angle = 360f * percentage;
    canvas.drawArc(mArc, DEFAULT_START_ANGLE, 360 - angle, true, mArcPaint);
  }

  private AnimationSet initHeartbeatAnimation() {
    AnimationSet swellAnimationSet = new AnimationSet(true);
    swellAnimationSet.addAnimation(new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f));
    swellAnimationSet.addAnimation(new AlphaAnimation(1.0f, 0.3f));
    swellAnimationSet.setDuration(500);
    swellAnimationSet.setInterpolator(new AccelerateInterpolator());
    swellAnimationSet.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        AnimationSet shrinkAnimationSet = new AnimationSet(true);
        shrinkAnimationSet.addAnimation(new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        shrinkAnimationSet.addAnimation(new AlphaAnimation(0.3f, 1.0f));
        shrinkAnimationSet.setDuration(500);
        shrinkAnimationSet.setInterpolator(new DecelerateInterpolator());
        shrinkAnimationSet.setFillAfter(false);
        shrinkAnimationSet.setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {
            isHeartBeating = false;
          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }
        });
        TickView.this.startAnimation(shrinkAnimationSet);// 动画结束时重新开始，实现心跳的View
      }
    });
    return swellAnimationSet;
  }

  public void startTick(long millisInFuture) {
    if (millisInFuture <= 0) {
      throw new IllegalArgumentException("millisInFuture cannot be 0 or <0");
    }
    mMillisInFuture = millisInFuture;
    if (isTicking) {
      mTimer.cancel();
    }
    mTimer = new CountDownTimer(mMillisInFuture, DEFAULT_TICK_INTERVAL) {
      @Override
      public void onTick(long millisUntilFinished) {
        mMillisRemianing = millisUntilFinished;
        if ((TEN_SECONDS_MILLIS > mMillisRemianing) && (!isHeartBeating)) {
          isHeartBeating = true;
          startAnimation(initHeartbeatAnimation());
        }
        invalidate();
      }

      @Override
      public void onFinish() {
        mMillisRemianing = 0;
        invalidate();
        stopTick();
        if (mListener != null) {
          mListener.onTickFinish();
        }
      }
    };
    mTimer.start();
    isTicking = true;
  }

  public void stopTick() {
    if (mTimer != null) {
      isTicking = false;
      mTimer.cancel();
    }
  }

  public void setmListener(OnTickFinishListener mListener) {
    this.mListener = mListener;
  }

  @Override
  protected void onDetachedFromWindow() {
    if (mTimer != null) {
      mTimer.cancel();
    }
    super.onDetachedFromWindow();
  }

  public interface OnTickFinishListener {
    void onTickFinish();
  }
}
