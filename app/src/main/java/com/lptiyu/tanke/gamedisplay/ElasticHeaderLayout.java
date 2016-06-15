package com.lptiyu.tanke.gamedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/31
 *
 * 用于首页RecyclerView的首项，能够根据用户手势绘制出弹簧效果，该View只实现了绘制逻辑，通过对setPercent来实现
 *
 * @author ldx
 */
public class ElasticHeaderLayout extends RelativeLayout {

  Path path;
  Paint paint;

  private float percent;

  public ElasticHeaderLayout(Context context) {
    super(context);
    init();
  }

  public ElasticHeaderLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ElasticHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setWillNotDraw(false);
    if (isInEditMode()) {
      return;
    }
    path = new Path();
    paint = new Paint();
    post(new Runnable() {
      @Override
      public void run() {
        paint.setShader(new LinearGradient(
            0, 0, getWidth(), 0,
            new int[]{
                getResources().getColor(R.color.light_blue),
                getResources().getColor(R.color.middle_blue),
                getResources().getColor(R.color.dark_blue)
            }
            , null,
            Shader.TileMode.REPEAT
        ));

        invalidate();
      }
    });

    paint.setAntiAlias(true);
  }

  public void setPercent(float percent) {
    percent = Math.min(1, percent);
    this.percent = percent;
    invalidate();
  }

  public float getPercent() {
    return percent;
  }

  /**
   * 可以通过调整这个来调整蓝白分界线的位置
   */
  private static final float sLines = 0.42f;

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int measuredWidth = getMeasuredWidth();
    int height = getHeight();
    path.reset();
    path.lineTo(0, sLines * height);
    path.quadTo(measuredWidth / 2f, (percent * 0.3f + sLines) * height, measuredWidth, sLines * height);
    path.lineTo(measuredWidth, 0);
    canvas.drawPath(path, paint);
  }

}
