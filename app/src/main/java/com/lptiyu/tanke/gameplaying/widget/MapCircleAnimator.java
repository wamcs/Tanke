package com.lptiyu.tanke.gameplaying.widget;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Classes used to make and add a circle animator on map
 * <p>
 * <p>
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/1/21
 *
 * @author ldx
 */
public class MapCircleAnimator {

  /**
   * The controller of map
   */
  private BaiduMap aMap;

  /**
   * The max radius of the circle, the unit is metre.
   */
  private float maxRadius = 10;

  private final ValueAnimator animator = generateAnimation();

  private final List<Circle> circleList = new ArrayList<>();

  private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

    float value = 0;

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      value = (Float) animation.getAnimatedValue() * maxRadius;
      value *= (1 << (20 - (int) aMap.getMapStatus().zoom));
      for (Circle circle : circleList) {
        circle.setRadius((int) value);
      }
    }
  };

  public MapCircleAnimator(BaiduMap aMap) {
    if (aMap == null) {
      throw new NullPointerException("Map is null");
    }
    this.aMap = aMap;
  }

  private ValueAnimator generateAnimation() {

    //TODO : to draw circle with the map system
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.setDuration(2000);
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    animator.setRepeatCount(ValueAnimator.INFINITE);
    animator.setRepeatMode(ValueAnimator.RESTART);
    return animator;
  }

  public void setMaxRadius(float maxRadius) {
    if (maxRadius <= 0) {
      throw new IllegalArgumentException("Wrong max radius");
    }
    this.maxRadius = maxRadius;
  }

  public float getMaxRadius() {
    return maxRadius;
  }

  public Circle addCircleAnimation(CircleOptions options) {
    Circle circle = ((Circle) aMap.addOverlay(options));
    circleList.add(circle);
    return circle;
  }

  public void removeCircle(Circle circle) {
    circleList.remove(circle);
  }

  /**
   * Generate a new {@link CircleOptions} with default params
   *
   * @param latLng the center of the circle
   * @return The new CircleOptions generated
   */
  public static CircleOptions defaultCircle(LatLng latLng) {
    return new CircleOptions().center(latLng)
        .radius(4000).stroke(new Stroke(25, Color.argb(50, 1, 1, 1)))
        .fillColor(Color.argb(50, 1, 1, 1));
  }

  public void start() {
    if (animator.isStarted()) {
      reset();
    }
    animator.addUpdateListener(animatorUpdateListener);
    animator.start();
  }

  public void reset() {
    animator.removeAllUpdateListeners();
    animator.cancel();
  }
}
