package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.utils.Display;
import com.lptiyu.tanke.widget.NumNail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class MapHelper {

  private Context mContext;

  private TextureMapView mapView;
  private BaiduMap mBaiduMap;

  private SensorHelper mSensorHelper;
  private MapCircleAnimationHelper mapCircleAnimationHelper;

  private List<Point> mPoints;
  private Map<Point, MarkerOptions> nailMarkerContainer = new HashMap<>();

  private LatLng currentLatLng;
  private LatLng lastTimeLatLng;
  private List<LatLng> trackLatLngs;

  private Circle attackPointCircle;
  private CircleOptions attackPointCircleOptions;

  private MyLocationData.Builder mLocationDataBuilder;
  private boolean animateToCurrentPositionOnce = true;

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
    init();
  }

  private void init() {
    trackLatLngs = new ArrayList<>();
    mSensorHelper = new SensorHelper(mContext);
    mapCircleAnimationHelper = new MapCircleAnimationHelper(mContext, mBaiduMap);

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

  public void startAnimate() {

  }

  public void bindData(List<Point> points) {
    if (points == null) {
      return;
    }
    mPoints = points;
    initNails(mPoints);
    setAttackPointCircle(mPoints.get(0));
  }

  /**
   * receive location info from LocateHelper
   * set the BDLocation to map as user's location
   *
   * @param location
   */
  public void onReceiveLocation(BDLocation location) {
    mBaiduMap.setMyLocationData(makeUpLocationData(location));
    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    if (animateToCurrentPositionOnce) {
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentLatLng, 18));
      animateToCurrentPositionOnce = false;
    }
    if ((null != lastTimeLatLng) && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) > Conf.LOCATION_DISTANCE_THRESHOLD_BOTTOM)
        && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) < Conf.LOCATION_DISTANCE_THRESHOLD_TOP)) {
      drawPolyLine(lastTimeLatLng);
    }
    lastTimeLatLng = currentLatLng;
  }

  public void animateCameraToCurrentPosition() {
    animateToCurrentPositionOnce = true;
  }

  /**
   * init the nail on the map
   * point info from mPoints
   */
  private void initNails(List<Point> points) {
    /**
     * init the first target
     * when user arrives in the current target
     * the next target will be shown
     */
    setNail(points.get(0), 0, NumNail.NailType.RED);
  }

  private void setAttackPointCircle(Point point) {
    if (attackPointCircle == null) {
      attackPointCircleOptions = generateCircleOption(new LatLng(point.getLatitude(), point.getLongitude()));
      attackPointCircle = mapCircleAnimationHelper.addCircleAnimation(attackPointCircleOptions);
    } else {
      attackPointCircle.setCenter(new LatLng(point.getLatitude(), point.getLongitude()));
    }
  }

  private void setNail(Point point, int index, NumNail.NailType type) {
    if (nailMarkerContainer == null) {
      throw new IllegalStateException("MarkerContainer not init");
    }
    MarkerOptions options;
    if (nailMarkerContainer.containsKey(point)) {
      options = nailMarkerContainer.get(point);
    } else {
      options = newNailMarkerOptions();
    }
    options.position(new LatLng(point.getLatitude(), point.getLongitude()));
    Bitmap b = NumNail.getNailBitmap(mContext, type).bakeText("" + index).getBitmap();
    if (b != null) {
      options.icon(BitmapDescriptorFactory.fromBitmap(b));
    }
    nailMarkerContainer.put(point, options);
    mBaiduMap.addOverlay(options);
  }

  public void drawPolyLine(LatLng ll) {
    if (ll == null) {
      return;
    }
    trackLatLngs.add(ll);
    mBaiduMap.addOverlay(initPolyLineOptions().points(trackLatLngs));
  }

  private PolylineOptions initPolyLineOptions() {
    return new PolylineOptions().width(10).color(0xff0F8AD7);
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
    mLocationDataBuilder
        .direction(mSensorHelper.getCurrentDegree())
        .latitude(location.getLatitude())
        .longitude(location.getLongitude());
    return mLocationDataBuilder.build();
  }

  private MyLocationConfiguration initMyLocationConfiguration() {
    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.img_user_position_arrow);
    return new MyLocationConfiguration(null, true, bitmapDescriptor);
  }

  private MarkerOptions newNailMarkerOptions() {
    MarkerOptions options = new MarkerOptions();
    options.title("Nail").draggable(false).anchor(0.28f, 0.85f);
    return options;
  }

  private CircleOptions generateCircleOption(LatLng latLng) {
    CircleOptions circleOptions = new CircleOptions();
    circleOptions
        .center(latLng)
        .fillColor(mContext.getResources().getColor(R.color.attackPointCircleColor));
    return circleOptions;
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

  public void onResume() {
    mapView.onResume();
    mSensorHelper.onResume();
    mapCircleAnimationHelper.onResume();
  }

  public void onPause() {
    mapView.onPause();
    mSensorHelper.onPause();
    mapCircleAnimationHelper.onPause();
  }

  public void onDestroy() {
    mapView.onDestroy();
    mSensorHelper.onDestroy();
    mapCircleAnimationHelper.onDestroy();
  }
}
