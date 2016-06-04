package com.lptiyu.tanke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-4
 *         email: wonderfulifeel@gmail.com
 */
public class TickView extends View{

  private int tickTextColor;
  private int tickInnerCircleColor;
  private int tickOuterCircleColor;
  private int tickFillColor;

  private int tickTextSize;
  private float tickInnerCircleRadius;
  private float tickOuterCircleRadius;
  private float tickFillWidth;

  private float mHeight;
  private float mWidth;

  private Rect mTextRect;
  private Paint mPaint;

  private String tickTextFormatter;

  private static final int DEFAULT_TEXT_COLOR = 0xffffff;
  private static final int DEFAULT_INNER_CIRCLE_COLOR = 0x769df6;
  private static final int DEFAULT_OUTER_CIRCLE_COLOR = 0xffffff;
  private static final int DEFAULT_FILL_COLOR = 0x40b553;
  private static final int DEFAULT_TEXT_SIZE = 12;
  private static final float DEFAULT_INNER_CIRCLE_RADIUS = Display.dip2px(30);
  private static final float DEFAULT_OUTER_CIRCLE_RADIUS = Display.dip2px(40);
  private static final float DEFAULT_FILL_WIDTH = Display.dip2px(3);

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
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mTextRect = new Rect();
    tickTextFormatter = getContext().getString(R.string.tick_view_message_formatter);
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
      mPaint.setTextSize(tickTextSize);
      mPaint.getTextBounds(tickTextFormatter, 0, tickTextFormatter.length(), mTextRect);
      int textWidth = mTextRect.width();
      Timber.e("textWidth : %d, inner radius : %f, outer radius : %f", textWidth, tickInnerCircleRadius, tickOuterCircleRadius);
      if (tickInnerCircleRadius < (textWidth >> 1)) {
        tickInnerCircleRadius = (textWidth >> 1) + DEFAULT_FILL_WIDTH;
      }
      if (tickInnerCircleRadius > tickOuterCircleRadius) {
        tickOuterCircleRadius = tickInnerCircleRadius + DEFAULT_FILL_WIDTH;
      }
      mWidth = tickOuterCircleRadius * 2 + getPaddingLeft() + getPaddingRight();
    }
    if (heightMode == MeasureSpec.EXACTLY) {
      mHeight = heightSize;
    }
    mHeight = Math.max(mWidth, mHeight);
    mWidth = mHeight;
    setMeasuredDimension((int) mWidth, (int) mHeight);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    float centerX = mWidth / 2;
    float centerY = mHeight / 2;

    drawOuterCircle(canvas, centerX, centerY);
    drawInnerCircle(canvas, centerX, centerY);
    drawTextAndFill(canvas, centerX, centerY);
  }

  private void drawOuterCircle(Canvas canvas, float x, float y) {
    mPaint.reset();
    mPaint.setAntiAlias(true);
    mPaint.setColor(tickOuterCircleColor);
    canvas.drawCircle(x, y, tickOuterCircleRadius, mPaint);
    Timber.e("x : %f, y : %f, radius : %f", x, y, tickOuterCircleRadius);

  }

  private void drawInnerCircle(Canvas canvas, float x, float y) {
    mPaint.reset();
    mPaint.setAntiAlias(true);
    mPaint.setColor(tickInnerCircleColor);
    canvas.drawCircle(x, y, tickInnerCircleRadius, mPaint);
  }

  private void drawTextAndFill(Canvas canvas, float x, float y) {
    mPaint.reset();
    mPaint.setAntiAlias(true);
    mPaint.setTextSize(tickTextSize);
    mPaint.setColor(tickTextColor);
    String content = String.format(tickTextFormatter, 18, 36);
    canvas.drawText(content, (mWidth - mPaint.measureText(content)) / 2, mHeight / 2 + mTextRect.height() / 2, mPaint);
  }

  private void playHeartbeatAnimation(final View heartbeatView) {
    AnimationSet swellAnimationSet = new AnimationSet(true);
    swellAnimationSet.addAnimation(new ScaleAnimation(1.0f, 1.8f, 1.0f, 1.8f, Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f));
    swellAnimationSet.addAnimation(new AlphaAnimation(1.0f, 0.3f));

    swellAnimationSet.setDuration(500);
    swellAnimationSet.setInterpolator(new AccelerateInterpolator());
    swellAnimationSet.setFillAfter(true);

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
        shrinkAnimationSet.addAnimation(new ScaleAnimation(1.8f, 1.0f, 1.8f, 1.0f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        shrinkAnimationSet.addAnimation(new AlphaAnimation(0.3f, 1.0f));
        shrinkAnimationSet.setDuration(1000);
        shrinkAnimationSet.setInterpolator(new DecelerateInterpolator());
        shrinkAnimationSet.setFillAfter(false);
        heartbeatView.startAnimation(shrinkAnimationSet);// 动画结束时重新开始，实现心跳的View
      }
    });
    heartbeatView.startAnimation(swellAnimationSet);
  }

  class TickCounterDownTimer extends CountDownTimer {

    public TickCounterDownTimer(long millisInFuture, long countDownInterval) {
      super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }

  }

}
