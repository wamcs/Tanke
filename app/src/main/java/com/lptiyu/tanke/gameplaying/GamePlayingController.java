package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class GamePlayingController extends ActivityController {

//  @BindView(R.id.map_view)
//  TextureMapView mapView;

  private MapHelper mapHelper;

  public GamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
//    mapHelper = new MapHelper(mapView);

  }

  @Override
  public void onResume() {
    super.onResume();
//    mapHelper.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
//    mapHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
//    mapHelper.onDestroy();
  }

  @Override
  protected boolean isToolbarEnable() {
    return false;
  }
}
