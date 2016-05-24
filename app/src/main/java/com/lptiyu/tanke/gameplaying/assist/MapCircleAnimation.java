package com.lptiyu.tanke.gameplaying.assist;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;

import java.lang.annotation.Target;
import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
class MapCircleAnimation implements
    ValueAnimator.AnimatorUpdateListener {

  private WeakReference<Context> contextWeakReference;
  private WeakReference<BaiduMap> baiduMapWeakReference;

  private float currentCircleRadius = 0.0f;
  private float maxRadius = 80.0f;
  private int circleFillColor;

  private Circle mCircle;
  private ValueAnimator mValueAnimator;

  public MapCircleAnimation(Context context, BaiduMap baiduMap) {
    baiduMapWeakReference = new WeakReference<>(baiduMap);
    contextWeakReference = new WeakReference<>(context);
    mValueAnimator = generateAnimation();
    circleFillColor = contextWeakReference.get().getResources().getColor(R.color.white07);
  }

  @Override
  public void onAnimationUpdate(ValueAnimator animation) {
    currentCircleRadius = (Float) animation.getAnimatedValue() * maxRadius;
    int currentCircleFillColor = mCircle.getFillColor();
    mCircle.setRadius((int) currentCircleRadius);
    mCircle.setFillColor(Color.argb((int) (255 * (1 - (float) animation.getAnimatedValue())), Color.red(currentCircleFillColor),
        Color.green(currentCircleFillColor),
        Color.blue(currentCircleFillColor)));
  }

  public void start() {
    if (mValueAnimator.isStarted()) {
      return;
    }
    mValueAnimator.addUpdateListener(this);
    mValueAnimator.start();
  }

  @TargetApi(19)
  public void pause() {
    if (mValueAnimator.isPaused()) {
      return;
    }
    mValueAnimator.pause();
  }

  @TargetApi(19)
  public void resume() {
    if (!mValueAnimator.isPaused()) {
      return;
    }
    mValueAnimator.resume();
  }

  public void stop() {
    mValueAnimator.removeUpdateListener(this);
    mValueAnimator.cancel();
  }

  public void destroy() {
    mValueAnimator.removeUpdateListener(this);
    mValueAnimator.cancel();
    mCircle.remove();
  }

  public void setAnimationCircle(Circle circle) {
    if (circle == null) {
      return;
    }
    if (mCircle != null) {
      mCircle.remove();
    }
    mCircle = circle;
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
        .fillColor(circleFillColor);
    return circleOptions;
  }

  private ValueAnimator generateAnimation() {
    //TODO : to draw circle with the map system
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    animator.setDuration(4000);
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    animator.setRepeatCount(ValueAnimator.INFINITE);
    animator.setRepeatMode(ValueAnimator.RESTART);
    return animator;
  }
}
