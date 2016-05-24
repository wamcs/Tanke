package com.lptiyu.tanke.gameplaying.assist;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-24
 *         email: wonderfulifeel@gmail.com
 */
public class MapCircleAnimationHelper {

  private WeakReference<Context> contextWeakReference;
  private WeakReference<BaiduMap> baiduMapWeakReference;

  private Map<Circle, MapCircleAnimation> mCircleAnimationMap;

  public MapCircleAnimationHelper(Context context, BaiduMap map) {
    if (map == null) {
      throw new IllegalArgumentException("Invalid argument, the map can not be null");
    }
    baiduMapWeakReference = new WeakReference<>(map);
    contextWeakReference = new WeakReference<>(context);
    mCircleAnimationMap = new HashMap<>();
  }

  public Circle addCircleAnimation(@NonNull CircleOptions options) {
    Circle circle = (Circle) baiduMapWeakReference.get().addOverlay(options);
    MapCircleAnimation circleAnimation = new MapCircleAnimation(contextWeakReference.get(), baiduMapWeakReference.get());
    circleAnimation.setAnimationCircle(circle);
    mCircleAnimationMap.put(circle, circleAnimation);
    circleAnimation.start();
    return circle;
  }

  public void setCircleCenter(Circle circle, LatLng latLng) {
    if (circle == null || latLng == null) {
      return;
    }
    MapCircleAnimation circleAnimation = mCircleAnimationMap.get(circle);
    if (circleAnimation == null) {
      return;
    }
    circleAnimation.setAnimateCenter(latLng);
  }

  public void removeCircleAnimation(Circle circle) {
    MapCircleAnimation circleAnimation = mCircleAnimationMap.remove(circle);
    if (circleAnimation == null) {
      return;
    }
    circleAnimation.destroy();
  }

  public void startAll() {
    for (MapCircleAnimation a : mCircleAnimationMap.values()) {
      a.start();
    }
  }

  public void stopAll() {
    for (MapCircleAnimation a : mCircleAnimationMap.values()) {
      a.stop();
    }
  }

  public void destroyAll() {
    for (MapCircleAnimation a : mCircleAnimationMap.values()) {
      a.destroy();
    }
  }

  @TargetApi(19)
  public void pauseAll() {
    for (MapCircleAnimation a : mCircleAnimationMap.values()) {
      a.pause();
    }
  }

  @TargetApi(19)
  public void resumeAll() {
    for (MapCircleAnimation a : mCircleAnimationMap.values()) {
      a.resume();
    }
  }

  public void onPause() {
    if (Build.VERSION.SDK_INT >= 19) {
      pauseAll();
    } else {
      stopAll();
    }
  }

  public void onResume() {
    if (Build.VERSION.SDK_INT >= 19) {
      resumeAll();
    } else {
      startAll();
    }
  }

  public void onDestroy() {
    destroyAll();
  }

}
