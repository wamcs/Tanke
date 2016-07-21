package com.lptiyu.tanke.activities.baidumapmode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.database.DBGameRecord;
import com.lptiyu.tanke.database.DBGameRecordDao;
import com.lptiyu.tanke.database.DBHelper;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.BaseSpotScrollView;
import com.lptiyu.tanke.widget.TickView;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/7/20
 * email:kaili@hustunique.com
 */
public class GameMapShowController extends ActivityController implements
    BaseSpotScrollView.OnSpotItemClickListener,
    BDLocationListener {


  @BindView(R.id.map_view)
  TextureMapView mapView;
  @BindView(R.id.tick_view)
  TickView mTickView;
  @BindView(R.id.zoom_in)
  ImageView mZoomIn;
  @BindView(R.id.zoom_out)
  ImageView mZoomOut;


  long gameId;
  long teamId;

  int currentAttackPointCount = 1;
  Point currentAttackPoint;
  List<Point> mPoints;
  List<LatLng> mPonitLatLngs;

  MapHelper mapHelper;
  LocateHelper locateHelper;

  GameZipHelper gameZipHelper;


  public GameMapShowController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
    try {
      readResume();
    } catch (SQLDataException e) {
      e.printStackTrace();
      finish();
    }
    initView();
  }

  private void init() {

    gameZipHelper = new GameZipHelper();

    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
    teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
    gameZipHelper.checkAndParseGameZip(gameId);

    mPoints = gameZipHelper.getmPoints();
    mPonitLatLngs = new ArrayList<>();
    mapHelper = new MapHelper(getActivity(), mapView);
    mapHelper.bindData(mPoints);

    locateHelper = new LocateHelper(getActivity().getApplicationContext());
    locateHelper.registerLocationListener(this);

  }

  private void initView() {

    for (Point p : mPoints.subList(0, currentAttackPointCount)) {
      mapHelper.showNextPoint(mPoints.indexOf(p));
      mPonitLatLngs.add(new LatLng(p.getLatitude(), p.getLongitude()));
    }
    mapHelper.drawPolyLine(mPonitLatLngs);
    moveToTarget();
  }

  private void readResume() throws SQLDataException {
    if (currentAttackPointCount > 1) {
      return;
    }
    List<DBGameRecord> gameRecordList = DBHelper.getInstance().getDBGameRecordDao()
        .queryBuilder().where(DBGameRecordDao.Properties.Game_id.eq(gameId)).list();
    if (gameRecordList.size() > 1) {
      Timber.e("game_id should be only,but size is %d", gameRecordList.size());
      throw new SQLDataException("game_id should be only");
    } else if (gameRecordList.size() == 0) {
      currentAttackPointCount = 1;
      return;
    }
    DBGameRecord gameRecord = gameRecordList.get(0);
    currentAttackPointCount = gameRecord.getRecord_text().size();
  }


  @OnClick(R.id.zoom_in)
  void zoomIn() {
    if (mapHelper.mapZoomIn()) {
      mZoomOut.setClickable(true);
    } else {
      mZoomIn.setClickable(false);
    }
  }

  @OnClick(R.id.zoom_out)
  void zoomOut() {
    if (mapHelper.mapZoomOut()) {
      mZoomIn.setClickable(true);
    } else {
      mZoomOut.setClickable(false);
    }
  }


  @OnClick(R.id.move_to_target)
  void moveToTarget() {
    if (mapHelper != null) {
      mapHelper.animateCameraToCurrentTarget();
    }
  }

  @OnClick(R.id.start_locate)
  void startLocateButtonClicked() {
    PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
  }

  /**
   * This method is invoked when spot is clicked
   * need to animate camera to clicked spot's marker
   *
   * @param view     clicked spot view
   * @param position the position of clicked spot view
   */
  @Override
  public void onSpotItemClick(View view, int position) {
    if (mPoints == null) {
      return;
    }
    int result = position - currentAttackPoint.getPointIndex();
    if (result < 0) {
      mapHelper.animateCameraToMarkerByIndex(position);
      mapHelper.showPointInfoWindow(mPoints.get(position));
    } else if (result == 0) {
      mapHelper.animateCameraToMarkerByIndex(position);
      mapHelper.showPointInfoWindow(mPoints.get(position));
    } else {
      ToastUtil.TextToast("攻击点还未开启");
    }
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocateService() {
    locateHelper.startLocate();
    mapHelper.animateCameraToCurrentPosition();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mapHelper != null) {
      mapHelper.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mapHelper != null) {
      mapHelper.onPause();
    }
  }

  @Override
  public void onDestroy() {
    if (mapHelper != null) {
      mapHelper.onDestroy();
    }
    if (locateHelper != null) {
      locateHelper.stopLocate();
    }
    super.onDestroy();
  }


  @Override
  protected boolean isToolbarEnable() {
    return false;
  }

  @Override
  public void onReceiveLocation(BDLocation bdLocation) {
    mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()), 20),
        1000);
  }
}
