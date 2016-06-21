package com.lptiyu.tanke.gamedetails;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-18
 *         email: wonderfulifeel@gmail.com
 */
public class GameDetailsLocationController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  CustomTextView mToolbarTitle;
  @BindView(R.id.map_view)
  TextureMapView mapView;

  private BaiduMap map;
  private LatLng mTargetLatLng;

  private Marker mTargetMarker;

  private static final double DEFAULT_LATITUDE = 34.123123;
  private static final double DEFAULT_LONGITUDE = 114.321321;

  public GameDetailsLocationController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.game_details_location));
    map = mapView.getMap();
    UiSettings uiSettings = map.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setRotateGesturesEnabled(false);
    uiSettings.setOverlookingGesturesEnabled(false);

    //TODO : add marker as the game location
    Intent intent = getIntent();
    double latitude = intent.getDoubleExtra(Conf.LATITUDE, DEFAULT_LATITUDE);
    double longitude = intent.getDoubleExtra(Conf.LONGITUDE, DEFAULT_LONGITUDE);
    mTargetLatLng = new LatLng(latitude, longitude);

    map.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
      @Override
      public void onMapLoaded() {
        map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(mTargetLatLng, 20), 1000);
        mTargetMarker = ((Marker) map.addOverlay(initMarkerOption()));
      }
    });
  }

  private MarkerOptions initMarkerOption() {
    MarkerOptions options = new MarkerOptions();
    return options
        .title("title")
        .position(mTargetLatLng)
        .draggable(false)
        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.nail_red))
        .animateType(MarkerOptions.MarkerAnimateType.drop);
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

}
