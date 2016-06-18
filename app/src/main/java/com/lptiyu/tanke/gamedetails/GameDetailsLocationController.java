package com.lptiyu.tanke.gamedetails;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
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
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

}
