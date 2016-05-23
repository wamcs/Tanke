package com.lptiyu.tanke.gameplaying;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.lptiyu.tanke.R;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class MapHelper {

  private TextureMapView mapView;
  private BaiduMap baiduMap;

  public MapHelper(TextureMapView view) {
    if (view == null) {
      throw new IllegalArgumentException("The map view is null");
    }
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

  private MyLocationConfiguration initMyLocationConfiguration() {
    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
    return new MyLocationConfiguration(null, true, bitmapDescriptor);
  }
}
