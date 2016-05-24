package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.TextureMapView;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipScanner;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public class GamePlayingController extends ActivityController implements
    BDLocationListener {

  @BindView(R.id.map_view)
  TextureMapView mapView;

  private MapHelper mapHelper;
  private LocateHelper locateHelper;
  private GameZipHelper gameZipHelper;


  public GamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mapHelper = new MapHelper(getActivity(), mapView);
    locateHelper = new LocateHelper(getActivity().getApplicationContext());
    locateHelper.registerLocationListener(this);
    gameZipHelper = new GameZipHelper();
  }

  @Override
  public void onReceiveLocation(BDLocation location) {
    mapHelper.onReceiveLocation(location);
  }

  @OnClick(R.id.start_locate)
  void startLocateButtonClicked() {
    PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
  }

  @OnClick(R.id.start_animate)
  void startAnimateButtonClicked() {
    mapHelper.startAnimate();
    gameZipHelper.checkAndParseGameZip(1111111111, 1111111111);
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  void startLocateService() {
    locateHelper.startLocate();
    mapHelper.animateCameraToCurrentPosition();
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
    mapHelper.onDestroy();
    locateHelper.stopLocate();
    locateHelper.unRegisterLocationListener(this);
    super.onDestroy();
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
