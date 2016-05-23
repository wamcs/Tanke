package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class MapHelper {

  private TextureMapView mapView;
  private BaiduMap mBaiduMap;

  private boolean animateToCurrentPositionOnce = true;
  private MyLocationData.Builder mLocationDataBuilder;

  private SensorHelper mSensorHelper;

  private Context mContext;

  private static final int paddingLeft = 0;
  private static final int paddingTop = 0;
  private static final int paddingRight = 0;
  private static final int paddingBottom = Display.dip2px(100);

  public MapHelper(Context context, TextureMapView view) {
    if (view == null) {
      throw new IllegalArgumentException("The map view is null");
    }
    mContext = context;
    mapView = view;
    mBaiduMap = mapView.getMap();
    mSensorHelper = new SensorHelper(mContext);
    initMap();
    initEvent();
  }

  private void initMap() {
    UiSettings uiSettings = mBaiduMap.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setRotateGesturesEnabled(false);
    uiSettings.setOverlookingGesturesEnabled(false);
    mBaiduMap.setMyLocationEnabled(true);
    mBaiduMap.setMyLocationConfigeration(initMyLocationConfiguration());
    mBaiduMap.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    mapView.setLogoPosition(LogoPosition.logoPostionRightTop);
    mapView.showScaleControl(false);
  }

  private void initEvent() {

  }

  /**
   * receive location info from LocateHelper
   * set the BDLocation to map as user's location
   *
   * @param location
   */
  public void onReceiveLocation(BDLocation location) {
    mBaiduMap.setMyLocationData(makeUpLocationData(location));
    if (animateToCurrentPositionOnce) {
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
      animateToCurrentPositionOnce = false;
    }
  }

  public void onResume() {
    mapView.onResume();
    mSensorHelper.onResume();
  }

  public void onPause() {
    mapView.onPause();
    mSensorHelper.onPause();
  }

  public void onDestroy() {
    mapView.onDestroy();
    mSensorHelper.onDestroy();
  }

  /**
   * this method can hide the baidu logo
   * but baidu said the logo should not be hide
   * so i'm not sure if i will be killed by baidu :)
   */
  private void hideBaiduLogo() {
    View child = mapView.getChildAt(1);
    if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
      child.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * method to make up MyLocationData
   * with the latitude and longitude from BDLocation
   * and the direction from the sensor
   *
   * @param location
   * @return
   */
  private MyLocationData makeUpLocationData(BDLocation location) {
    if (mLocationDataBuilder == null) {
      mLocationDataBuilder = new MyLocationData.Builder();
    }
    mLocationDataBuilder.accuracy(location.getRadius())
        .direction(mSensorHelper.getCurrentDegree()).latitude(location.getLatitude())
        .longitude(location.getLongitude());
    return mLocationDataBuilder.build();
  }

  public void animateCameraToCurrentPosition() {
    animateToCurrentPositionOnce = true;
  }

  private MyLocationConfiguration initMyLocationConfiguration() {
    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.img_user_position_arrow);
    return new MyLocationConfiguration(null, true, bitmapDescriptor);
  }
}
