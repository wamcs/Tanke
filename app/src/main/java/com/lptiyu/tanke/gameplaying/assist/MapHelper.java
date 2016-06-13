package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
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

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class MapHelper implements
    BaiduMap.OnMarkerClickListener {

  private Context mContext;

  private TextureMapView mapView;
  private BaiduMap mBaiduMap;

  private SensorHelper mSensorHelper;

  private List<Point> mPoints;
  private Point currentAttackPoint;
  private Point clickedPoint;
  private Map<Point, Marker> nailMarkerContainer = new HashMap<>();

  private LatLng currentLatLng;
  private LatLng lastTimeLatLng;
  private List<LatLng> trackLatLngs;

  // info window content and info window, show when user arrive the attack point
  private InfoWindow mTaskEntryInfoWindow;
  private View mTaskEntryInfoWindowView;

  private MyLocationData.Builder mLocationDataBuilder;

  private OnMapMarkerClickListener mMapMarkerClickListener;

  // if this boolean is true, map will animate to current location when receive newly location
  private boolean animateToCurrentPositionOnce = true;

  //  private boolean isReachAttackPoint = false;
  private boolean isInfoWindowShown = false;

  private static final int paddingLeft = 0;
  private static final int paddingTop = 0;
  private static final int paddingRight = 0;
  private static final int paddingBottom = Display.dip2px(60);

  // default delta value, the distance between info window and marker
  private static final int DEFAULT_INFO_WINDOW_DELTA_Y = -70;

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
    trackLatLngs = new ArrayList<>(2);
    mSensorHelper = new SensorHelper(mContext);
    mTaskEntryInfoWindowView = LayoutInflater.from(mContext).inflate(R.layout.layout_map_infowindow, null);

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
    mBaiduMap.setOnMarkerClickListener(this);
  }

  public void bindData(List<Point> points) {
    if (points == null) {
      return;
    }
    mPoints = points;
  }

  public void initMapFlow() {
    initNails(mPoints);
    currentAttackPoint = mPoints.get(0);
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    if (isInfoWindowShown) {
      mBaiduMap.hideInfoWindow();
      isInfoWindowShown = false;
      return true;
    }
    clickedPoint = null;
    for (Map.Entry<Point, Marker> entry : nailMarkerContainer.entrySet()) {
      if (entry.getValue() == marker) {
        clickedPoint = entry.getKey();
      }
    }
    if (clickedPoint == null) {
      return true;
    }

    mTaskEntryInfoWindowView.findViewById(R.id.start_task).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mMapMarkerClickListener != null) {
          mMapMarkerClickListener.onMarkerClicked(clickedPoint);
          mBaiduMap.hideInfoWindow();
        }
      }
    });
    mTaskEntryInfoWindow = new InfoWindow(mTaskEntryInfoWindowView, marker.getPosition(), DEFAULT_INFO_WINDOW_DELTA_Y);
    mBaiduMap.showInfoWindow(mTaskEntryInfoWindow);
    isInfoWindowShown = true;
    return true;
  }

  public void onReachAttackPoint(int index) {
//    isReachAttackPoint = true;
    if (index < 0 || mPoints == null || index >= mPoints.size()) {
      return;
    }
    currentAttackPoint = mPoints.get(index);
    setNail(currentAttackPoint, index, NumNail.NailType.GREEN);
  }

  public void updateNextPoint(int index) {
//    isReachAttackPoint = false;
    if (index < 0 || mPoints == null || index >= mPoints.size()) {
      return;
    }
    currentAttackPoint = mPoints.get(index);
    setNail(currentAttackPoint, index, NumNail.NailType.RED);
  }

  /**
   * receive location info from LocateHelper
   * set the BDLocation to map as user's location
   *
   * @param location real time location from LocateHelper
   */
  public void onReceiveLocation(BDLocation location) {
    mBaiduMap.setMyLocationData(makeUpLocationData(location));
    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    if (animateToCurrentPositionOnce) {
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentLatLng, 20));
      animateToCurrentPositionOnce = false;
    }
    if ((null != lastTimeLatLng) && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) > Conf.LOCATION_DISTANCE_THRESHOLD_BOTTOM)
        && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) < Conf.LOCATION_DISTANCE_THRESHOLD_TOP)) {
      drawPolyLine(lastTimeLatLng, currentLatLng);
    }
    lastTimeLatLng = currentLatLng;
  }

  public void animateCameraToCurrentPosition() {
    animateToCurrentPositionOnce = true;
  }

  public void animateCameraToCurrentTarget() {
    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentAttackPoint.getLatLng(), 20));
  }

  public void animateCameraToMarkerByIndex(int index) {
    if (mPoints != null && index <= currentAttackPoint.getPointIndex()) {
      Point targetPoint = mPoints.get(index);
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(targetPoint.getLatLng(), 20));
    }
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

  private void setNail(Point point, int index, NumNail.NailType type) {
    if (nailMarkerContainer == null) {
      throw new IllegalStateException("MarkerContainer not init");
    }
    MarkerOptions options;
    if (nailMarkerContainer.containsKey(point)) {
      nailMarkerContainer.get(point).remove();
    }
    options = newNailMarkerOptions();
    options.position(point.getLatLng());
    Bitmap b = NumNail.getNailBitmap(mContext, type).bakeText("" + index).getBitmap();
    if (b != null) {
      options.icon(BitmapDescriptorFactory.fromBitmap(b));
    }
    nailMarkerContainer.put(point, ((Marker) mBaiduMap.addOverlay(options)));
  }

  private void drawPolyLine(LatLng oldLatLng, LatLng newLatLng) {
    if (oldLatLng == null || newLatLng == null) {
      return;
    }
    trackLatLngs.clear();
    trackLatLngs.add(oldLatLng);
    trackLatLngs.add(newLatLng);
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
    options
        .title("Nail")
        .draggable(false)
        .anchor(0.28f, 0.85f);
    return options;
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
  }

  public void onPause() {
    mapView.onPause();
    mSensorHelper.onPause();
  }

  public void onDestroy() {
    mapView.onDestroy();
    mSensorHelper.onDestroy();
  }

  public BaiduMap getmBaiduMap() {
    return mBaiduMap;
  }

  public void setmMapMarkerClickListener(OnMapMarkerClickListener mMapMarkerClickListener) {
    this.mMapMarkerClickListener = mMapMarkerClickListener;
  }

  public interface OnMapMarkerClickListener {
    void onMarkerClicked(Point point);
  }

}
