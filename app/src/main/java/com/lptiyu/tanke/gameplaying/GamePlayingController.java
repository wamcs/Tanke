package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class GamePlayingController extends ActivityController {

  @BindView(R.id.map_view)
  TextureMapView mapView;

  private MapHelper mapHelper;


  public GamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mapHelper = new MapHelper(getActivity(), mapView);
  }

  @OnClick(R.id.start_locate)
  void startLocateButtonClicked() {
    PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  void startLocateService() {
    Timber.e("start locate");
  }

  @Override
  public void onResume() {
    super.onResume();
    mapHelper.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapHelper.onDestroy();
  }

  @Override
  protected boolean isToolbarEnable() {
    return false;
  }

  @Override
  public void onBackPressed() {
    finish();
  }

}
