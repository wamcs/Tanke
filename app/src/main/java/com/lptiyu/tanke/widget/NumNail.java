package com.lptiyu.tanke.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.lptiyu.tanke.R;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/1/21
 * <p>
 * The NumNail is a bitmap which contains a nail with some text, especially a number.
 * You can use constructor to construct the bitmap, and use {@link #bakeText(String)}
 * to draw text on this bitmap.
 *
 * @author ldx
 */
public abstract class NumNail {

  /**
   * The paint to draw the text
   */
  public Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  /**
   * Base context
   */
  private Context mContext;

  /**
   * Text size, unit is sp
   */
  private float mTextSize = 11;

  /**
   * The Canvas to draw text on
   */
  private Canvas mCanvas;

  /**
   * Canvas was attached on this bitmap, bitmap is a data struct in reality.
   */
  private Bitmap mBitmap;

  /**
   * The exact drawing rect
   */
  private RectF mRect;

  /**
   * Constructor
   *
   * @param context The context to get the resource, and other something.
   */
  public NumNail(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("The params in NumNail can't be null");
    }
    this.mContext = context;
    this.mBitmap = getDrawingBitmap();
    mCanvas = new Canvas(mBitmap);
    this.mRect = getDrawingRect();
    init();
  }

  private void init() {
    mPaint.setTextSize(
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
            , mTextSize, mContext.getResources().getDisplayMetrics()));
    //noinspection deprecation
    mPaint.setColor(getContext().getResources().getColor(R.color.default_font_color));
  }

  /**
   * Generate the Canvas to be drew something on
   *
   * @return The canvas to control the bitmap
   */
  public abstract Bitmap getDrawingBitmap();

  /**
   * Where should something to be drew
   *
   * @return The rectF to be drew on, you can draw everything in this rect
   */
  public abstract RectF getDrawingRect();

  /**
   * The Canvas, which controls a bitmap, will be drew the text on when you invoke this method.
   *
   * @param text The text to be drew
   */
  public NumNail bakeText(String text) {
    PointF pointF = getTextRect(mPaint, mRect, text);
    mCanvas.drawText(text, pointF.x, pointF.y, mPaint);
    return this;
  }

  public NumNail bakeImage(Bitmap bitmap){
    if (bitmap == null){
      return this;
    }
    int height = bitmap.getHeight();
    int canvasHeight = mCanvas.getHeight();
    mCanvas.drawBitmap(bitmap,0,canvasHeight-height,null);
    return this;
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  /**
   * @param paint Paint to draw the text
   * @param text  The text to be draw
   * @param rectF the rectF contains the text
   * @return The start point to draw the text
   */
  private PointF getTextRect(Paint paint, RectF rectF, String text) {
    //unit dimension
    float width = paint.measureText(text);
//    float height = paint.getTextSize();
//    Timber.d("The textsize width is %f, height is %f", width, height);
    //In vivo , it's 16.5
    //In nexus, it's width = 19 and height = 33

    Rect bounds = new Rect();
    paint.getTextBounds(text, 0, text.length(), bounds);
//    Timber.d("The bounds width = %d, height = %d", bounds.width(), bounds.height());
    //In vivo, it's 13
    //In nexus, it's width = 16 and height = 25
    PointF pointF = new PointF();

    pointF.x = rectF.left + (rectF.width() - width) / 2f;
    pointF.y = rectF.bottom - (rectF.height() - bounds.height()) / 2f;
    return pointF;
  }

  public float getTextSize() {
    return mTextSize;
  }

  public void setTextSize(float textSize) {
    this.mTextSize = textSize;
  }

  protected Context getContext() {
    return mContext;
  }

  /**
   * Default version of {@link #getNailBitmap(Context, NailType)}, make it red.
   */
  public static NumNail getNailBitmap(Context context) {
    return getNailBitmap(context, NailType.RED);
  }

  /**
   * Factory methods to construct the NumNail.
   *
   * @param context the context
   * @param type    0 is red, 1 is green, 2 is grey
   * @return the NumNail
   */
  public static NumNail getNailBitmap(Context context, NailType type) {
    if (type == NailType.RED) {
      return new NumNailImpl(context);
    } else if (type == NailType.GREEN) {
      return new NumNailGreenImpl(context);
    } else if (type == NailType.GREY) {
      return new NumNailGreyImpl(context);
    } else if (type == NailType.IMAGE){
      return new NumNailImgImpl(context);
    }else {
      throw new IllegalStateException("type must be 0, 1, 2. 0 is red, 1 is green, 2 is grey");
    }
  }

  public enum NailType {
    RED,
    GREEN,
    GREY,
    IMAGE
  }

  private static class NumNailGreyImpl extends NumNail {

    private static Bitmap cache;

    /**
     * Constructor, which will call {@link #getDrawingBitmap()} and {@link #getDrawingRect()}, the
     * previous method may cost time.
     *
     * @param context The context to get the resource, and other something.
     */
    public NumNailGreyImpl(Context context) {
      super(context);
    }

    protected Bitmap getCache() {
      if (cache == null || cache.isRecycled()) {
        cache = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.spot_to_do);
      }
      return cache;
    }

    @Override
    public Bitmap getDrawingBitmap() {
      Bitmap cache = getCache();
      return cache.copy(cache.getConfig(), true);
    }

    @Override
    public RectF getDrawingRect() {
      int densityDpi = getContext().getResources().getDisplayMetrics().densityDpi;
      return new RectF(0, 0, 60 * densityDpi / 480f, 60 * densityDpi / 480f);
    }

  }

  private static class NumNailGreenImpl extends NumNail {

    private static Bitmap cache;

    /**
     * Constructor, which will call {@link #getDrawingBitmap()} and {@link #getDrawingRect()}, the
     * previous method may cost time.
     *
     * @param context The context to get the resource, and other something.
     */
    public NumNailGreenImpl(Context context) {
      super(context);
    }

    protected Bitmap getCache() {
      if (cache == null || cache.isRecycled()) {
        cache = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.spot_finished);
      }
      return cache;
    }

    @Override
    public Bitmap getDrawingBitmap() {
      Bitmap cache = getCache();
      return cache.copy(cache.getConfig(), true);
    }

    @Override
    public RectF getDrawingRect() {
      int densityDpi = getContext().getResources().getDisplayMetrics().densityDpi;
      return new RectF(0, 0, 60 * densityDpi / 480f, 60 * densityDpi / 480f);
    }
  }


  private static class NumNailImpl extends NumNail {

    private static Bitmap cache;

    /**
     * Constructor, which will call {@link #getDrawingBitmap()} and {@link #getDrawingRect()}, the
     * previous method may cost time.
     *
     * @param context The context to get the resource, and other something.
     */
    public NumNailImpl(Context context) {
      super(context);
    }

    protected Bitmap getCache() {
      if (cache == null || cache.isRecycled()) {
        cache = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.spot_doing);
        Timber.d("The length of cache width = %d height = %d", cache.getWidth(), cache.getHeight());
        //In Nexus(1080 * 1920), the cache's width is 80 and height is 89
        //And in vivo(an 720p android phone), the cache's width is 40 and height is 45
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        Timber.d("The density is %d", metrics.densityDpi);
        //Density of nexus is 480
        //Density of vivo is 240
        Timber.d("The Scaled length may be help, the DisplayMetrics is so the cache's scaled width = %d and height = %d",
            cache.getScaledWidth(metrics),
            cache.getScaledHeight(metrics));
        //In nexus, scaled width and height is 80 and 89
        //In vivo , scaled width and height is 40 and 45
      }
      return cache;
    }

    @Override
    public Bitmap getDrawingBitmap() {
      Bitmap cache = getCache();
      return cache.copy(cache.getConfig(), true);
    }

    @Override
    public RectF getDrawingRect() {
      int densityDpi = getContext().getResources().getDisplayMetrics().densityDpi;
      return new RectF(0, 0, 60 * densityDpi / 480f, 60 * densityDpi / 480f);
    }
  }

  private static class NumNailImgImpl extends NumNail {
    private static Bitmap cache;

    /**
     * Constructor
     *
     * @param context The context to get the resource, and other something.
     */
    public NumNailImgImpl(Context context) {
      super(context);
    }

    protected Bitmap getCache() {
      if (cache == null || cache.isRecycled()) {
        cache = Bitmap.createBitmap(
            getContext().getResources().getDimensionPixelOffset(R.dimen.x180),
            getContext().getResources().getDimensionPixelOffset(R.dimen.y180),
            Bitmap.Config.ARGB_4444);
        Timber.d("The length of cache width = %d height = %d", cache.getWidth(), cache.getHeight());
        //In Nexus(1080 * 1920), the cache's width is 80 and height is 89
        //And in vivo(an 720p android phone), the cache's width is 40 and height is 45
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        Timber.d("The density is %d", metrics.densityDpi);
        //Density of nexus is 480
        //Density of vivo is 240
        Timber.d("The Scaled length may be help, the DisplayMetrics is so the cache's scaled width = %d and height = %d",
            cache.getScaledWidth(metrics),
            cache.getScaledHeight(metrics));
        //In nexus, scaled width and height is 80 and 89
        //In vivo , scaled width and height is 40 and 45
      }
      return cache;
    }

    @Override
    public Bitmap getDrawingBitmap() {
      Bitmap cache = getCache();
      return cache.copy(cache.getConfig(), true);
    }

    @Override
    public RectF getDrawingRect() {
      int densityDpi = getContext().getResources().getDisplayMetrics().densityDpi;
      return new RectF(0, 0, 60 * densityDpi / 480f, 60 * densityDpi / 480f);
    }
  }
}
