package com.lptiyu.tanke.gamedisplay;


import android.view.animation.Interpolator;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/31
 *
 * Used for jellyView on the Header of RecyclerView
 *
 * @author ldx
 */
public class ElasticOutInterpolator implements Interpolator {

  @Override
  public float getInterpolation(float i) {
    if (i == 0) return 0;
    if (i == 1) return 1;
    return (float)
        (Math.pow(2, -5 * i) *// 这是一个衰减函数，可以调整 -5来调整衰减速度
            Math.sin((i - 0.075) * 5 * Math.PI) // 这是一个正弦函数，可以通过修改 5 来调整震荡次数
            + 1);
  }
}
