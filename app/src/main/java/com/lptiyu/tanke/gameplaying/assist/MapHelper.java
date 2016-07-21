package com.lptiyu.tanke.gameplaying.assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.gameplaying.pojo.Point;
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

  private List<Point> mPoints;
  private Point currentAttackPoint;
  private PolylineOptions polyline;
  private Map<Point, Marker> nailMarkerContainer = new HashMap<>();

  private LatLng currentLatLng;
  private LatLng lastTimeLatLng;
  private List<LatLng> trackLatLngs;

  // info window content and info window, show when user arrive the attack point
  private Map<Marker, InfoWindow> markerInfoWindowMap;

  private MyLocationData.Builder mLocationDataBuilder;

  private OnMapMarkerClickListener mMapMarkerClickListener;

  // if this boolean is true, map will animate to current location when receive newly location
  private boolean animateToCurrentPositionOnce = true;


  // default delta value, the distance between info window and marker
  private static final int DEFAULT_INFO_WINDOW_DELTA_Y = Display.dip2px(-25);
  public static final int DEFAULT_ANIMATION_DURATION = 1000;
  private LinearLayout.LayoutParams params;

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
    markerInfoWindowMap = new HashMap<>();
    params = new LinearLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.x260),
        (int) mContext.getResources().getDimension(R.dimen.y180));
    initMap();
  }

  private void initMap() {
    UiSettings uiSettings = mBaiduMap.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setRotateGesturesEnabled(false);
    uiSettings.setOverlookingGesturesEnabled(false);
    mBaiduMap.setMyLocationEnabled(true);
    mBaiduMap.setMyLocationConfigeration(initMyLocationConfiguration());
    mapView.setLogoPosition(LogoPosition.logoPostionRightTop);
    mapView.showZoomControls(false);
    mapView.showScaleControl(false);
    hideBaiduLogo();
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

  public boolean mapZoomIn() {
    MapStatus oldMapStatus = mBaiduMap.getMapStatus();
    float zoom = oldMapStatus.zoom;
    float maxZoom = mBaiduMap.getMaxZoomLevel();
    if (zoom >= maxZoom) {
      return false;
    }
    MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(oldMapStatus.target, zoom + 1);
    mBaiduMap.animateMapStatus(update, DEFAULT_ANIMATION_DURATION);
    return true;
  }

  public boolean mapZoomOut() {
    MapStatus oldMapStatus = mBaiduMap.getMapStatus();
    float zoom = oldMapStatus.zoom;
    float minZoom = mBaiduMap.getMinZoomLevel();
    if (zoom <= minZoom) {
      return false;
    }
    MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(oldMapStatus.target, zoom - 1);
    mBaiduMap.animateMapStatus(update, DEFAULT_ANIMATION_DURATION);
    return true;
  }

  public void showPointInfoWindow(Point point) {
    Marker marker = nailMarkerContainer.get(point);
    if (marker != null) {

      InfoWindow infoWindow = markerInfoWindowMap.get(marker);
      if (infoWindow == null) {
        View infoWindowView = LayoutInflater.from(mContext).inflate(R.layout.layout_map_infowindow, null);
        ImageView imageView = ((ImageView) infoWindowView.findViewById(R.id.start_task));
        imageView.setImageURI(Uri.parse(point.getIntroImage()));
        imageView.setLayoutParams(params);
        infoWindow = new InfoWindow(infoWindowView, marker.getPosition(), DEFAULT_INFO_WINDOW_DELTA_Y);
        markerInfoWindowMap.put(marker, infoWindow);
        mBaiduMap.showInfoWindow(infoWindow);
      } else {
        mBaiduMap.showInfoWindow(infoWindow);
      }

    }
  }

  public void onReachAttackPoint(int index) {
    //    isReachAttackPoint = true;
    if (index < 0 || mPoints == null || index >= mPoints.size()) {
      return;
    }
    currentAttackPoint = mPoints.get(index);
    setNail(currentAttackPoint, index, NumNail.NailType.GREEN, null);
  }

  public void updateNextPoint(int index) {
    //    isReachAttackPoint = false;
    if (index < 0 || mPoints == null || index >= mPoints.size()) {
      return;
    }
    currentAttackPoint = mPoints.get(index);
    setNail(currentAttackPoint, index, NumNail.NailType.RED, null);
  }

  public void showNextPoint(int index) {
    if (index < 0 || mPoints == null || index >= mPoints.size()) {
      return;
    }
    currentAttackPoint = mPoints.get(index);
    setNail(currentAttackPoint, index, NumNail.NailType.IMAGE, BitmapFactory.decodeFile(Uri.parse(currentAttackPoint.getIntroImage()).getPath()));
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
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentLatLng, 20),
          DEFAULT_ANIMATION_DURATION);
      animateToCurrentPositionOnce = false;
    }
    //    if (currentAttackPoint != null && currentAttackPoint.getPointIndex() != 0) {
    //      if ((null != lastTimeLatLng) && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) > Conf
    // .LOCATION_DISTANCE_THRESHOLD_BOTTOM)
    //          && (DistanceUtil.getDistance(lastTimeLatLng, currentLatLng) < Conf
    // .LOCATION_DISTANCE_THRESHOLD_TOP)) {
    //        drawPolyLine(lastTimeLatLng, currentLatLng);
    //      }
    //    }
    lastTimeLatLng = currentLatLng;
  }

  public void animateCameraToCurrentPosition() {
    animateToCurrentPositionOnce = true;
  }

  public void animateCameraToCurrentTarget() {
    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(currentAttackPoint.getLatLng(), 20),
        DEFAULT_ANIMATION_DURATION);
  }

  public void animateCameraToMarkerByIndex(int index) {
    if (mPoints != null && index <= currentAttackPoint.getPointIndex()) {
      Point targetPoint = mPoints.get(index);
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(targetPoint.getLatLng(), 20),
          DEFAULT_ANIMATION_DURATION);
    } else {

    }
  }

  public void drawHistoryTrack(List<LatLng> points) {
    if (points == null || points.size() == 0) {

    } else if (points.size() > 1) {
      polyline = new PolylineOptions().width(10)
          .color(Color.RED).points(points);
      mBaiduMap.addOverlay(polyline);
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
    setNail(points.get(0), 0, NumNail.NailType.RED, null);
  }


  private void setNail(Point point, int index, NumNail.NailType type, Bitmap bitmap) {
    if (nailMarkerContainer == null) {
      throw new IllegalStateException("MarkerContainer not init");
    }
    MarkerOptions options;
    if (nailMarkerContainer.containsKey(point)) {
      nailMarkerContainer.get(point).remove();
    }

    options = newNailMarkerOptions();
    options.position(point.getLatLng());
    Bitmap b;
    switch (type){
      case IMAGE:
        b = NumNail.getNailBitmap(mContext,type).bakeImage(bitmap).getBitmap();
        break;
      default:
        b = NumNail.getNailBitmap(mContext,type).bakeText(String.valueOf(index + 1)).getBitmap();
        break;
    }

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

  public void drawPolyLine(List<LatLng> points) {
    if (points.size() <= 1) {
      return;
    }
    mBaiduMap.addOverlay(initPolyLineOptions().points(points));
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
        .draggable(false);
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
