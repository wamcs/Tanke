package com.lptiyu.tanke.gameplaying.assist;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;

import java.lang.ref.WeakReference;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class MapCircleAnimation implements
    ValueAnimator.AnimatorUpdateListener {

  private float maxRadius = 10.0f;

  private WeakReference<Context> contextWeakReference;
  private WeakReference<BaiduMap> baiduMapWeakReference;

  private float currentCircleRadius = 0.0f;
  private Circle mCircle;
  private ValueAnimator mValueAnimator;

  public MapCircleAnimation(Context context, BaiduMap baiduMap) {
    if (baiduMap == null) {
      throw new IllegalArgumentException("Invalid argument, the map can not be null");
    }
    baiduMapWeakReference = new WeakReference<>(baiduMap);
    contextWeakReference = new WeakReference<>(context);
    mValueAnimator = generateAnimation();
  }

  @Override
  public void onAnimationUpdate(ValueAnimator animation) {
    currentCircleRadius = (Float) animation.getAnimatedValue() * maxRadius;
    currentCircleRadius *= (1 << (20 - (int) baiduMapWeakReference.get().getMapStatus().zoom));
    mCircle.setRadius((int) currentCircleRadius);
  }

  public void start() {
    if (mValueAnimator.isStarted()) {
      return;
    }
    mValueAnimator.addUpdateListener(this);
    mValueAnimator.start();
  }

  public void stop() {
    mValueAnimator.removeUpdateListener(this);
    mValueAnimator.cancel();
  }

  public void setAnimateCenter(LatLng latLng) {
    if (mCircle == null) {
      CircleOptions options = generateCircleOption(latLng);
      mCircle = (Circle) baiduMapWeakReference.get().addOverlay(options);
      return;
    }
    mCircle.setCenter(latLng);
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

  private CircleOptions generateCircleOption(LatLng latLng) {
    CircleOptions circleOptions = new CircleOptions();
    circleOptions
        .center(latLng)
        .stroke(new Stroke(0, Color.argb(50, 0, 0, 0)))
        .fillColor(contextWeakReference.get().getResources().getColor(R.color.white06));
    return circleOptions;
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
}
