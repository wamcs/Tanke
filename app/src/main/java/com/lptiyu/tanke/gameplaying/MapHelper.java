package com.lptiyu.tanke.gameplaying;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Display;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class MapHelper {

  private TextureMapView mapView;
  private BaiduMap baiduMap;

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
    baiduMap = mapView.getMap();
    initMap();
    initEvent();
  }

  private void initMap() {
    UiSettings uiSettings = baiduMap.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setRotateGesturesEnabled(false);
    uiSettings.setOverlookingGesturesEnabled(false);
    baiduMap.setMyLocationEnabled(true);
    baiduMap.setMyLocationConfigeration(initMyLocationConfiguration());
    baiduMap.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    mapView.setLogoPosition(LogoPosition.logoPostionRightTop);
    mapView.showScaleControl(false);


  }

  private void initEvent() {

  }

  public void onResume() {
    mapView.onResume();
  }

  public void onPause() {
    mapView.onPause();
  }

  public void onDestroy() {
    mapView.onDestroy();
  }

  // 隐藏logo
  private void hideBaiduLogo() {
    View child = mapView.getChildAt(1);
    if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
      child.setVisibility(View.INVISIBLE);
    }
  }

  private MyLocationConfiguration initMyLocationConfiguration() {
    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
    return new MyLocationConfiguration(null, true, bitmapDescriptor);
  }
}
