package com.lptiyu.tanke.gameplaying;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibrateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-22
 *         email: wonderfulifeel@gmail.com
 */
public abstract class GamePlayingController extends ActivityController implements
    BDLocationListener {

  @BindView(R.id.map_view)
  TextureMapView mapView;

  MapHelper mapHelper;
  LocateHelper locateHelper;
  GameZipHelper gameZipHelper;

  int currentAttackPointIndex = 0;
  Point currentAttackPoint;
  List<Point> mPoints;

  // used to create and dispatch record
  RecordsHandler mRecordsHandler;

  AlertDialog mAlertDialog;
  boolean isReachedAttackPoint = false;
  boolean isGameFinished = false;

  static final long TEMP_GAME_ID = 1000000001L;
  static final long TEMP_LINE_ID = 2000000001L;
  static final long TEMP_TEAM_ID = 3000000001L;

  public GamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    gameZipHelper = new GameZipHelper();
    if (!gameZipHelper.checkAndParseGameZip(TEMP_GAME_ID, TEMP_LINE_ID) || gameZipHelper.getmPoints().size() == 0) {
      ToastUtil.TextToast("游戏包加载失败");
      //TODO : notice user that the game zip is damaged, please download again,then finish this activity
    }

    //TODO : get game id and line id from intent
    ToastUtil.TextToast("游戏包加载完成");

    mapHelper = new MapHelper(getActivity(), mapView);
    locateHelper = new LocateHelper(getActivity().getApplicationContext());
    locateHelper.registerLocationListener(this);

    mPoints = gameZipHelper.getmPoints();
    mapHelper.bindData(mPoints);

    mRecordsHandler = new RecordsHandler.Builder(TEMP_GAME_ID, TEMP_TEAM_ID).build();

    initRecords();
  }

  @Override
  public void onReceiveLocation(BDLocation location) {
    mapHelper.onReceiveLocation(location);

    /**
     * Check whether user reach the attack point
     * when the game is not finished and the point is not reached
     */
    if (!isGameFinished && !isReachedAttackPoint) {
      if (checkIfReachAttackPoint(location)) {
        /**
         * TODO : when the point's tasks are done
         * remember to do these things
         * isReachedAttackPoint = false
         * currentAttackPointIndex++
         * currentAttackPoint = mPoint(currentAttackPointIndex)
         */

        isReachedAttackPoint = true;
        onReachAttackPoint();
        RecordsUtils.dispatchTypeRecord(mRecordsHandler, initReachPointRecord(location.getLatitude(), location.getLongitude(), currentAttackPoint.getId()));
      }
    }
  }

  /**
   * This method to notify user when they first arrive the attack point
   * vibrate and show the dialog to tell them click the nail in the map
   * then begin the tasks in this attack point
   */
  private void onReachAttackPoint() {
    if (currentAttackPoint == null || currentAttackPointIndex < 0) {
      Timber.e("current attack point is error");
      return;
    }
    mapHelper.onReachAttackPoint();
    VibrateUtils.vibrate();
    showAlertDialog(getString(R.string.reach_attack_point));
  }

  private RunningRecord initReachPointRecord(double x, double y, long pointId) {
    return new RunningRecord.Builder()
        .x(x)
        .y(y)
        .pointId(pointId)
        .type(RunningRecord.RECORD_TYPE.POINT_REACH)
        .createTime(System.currentTimeMillis())
        .build();
  }

  /**
   * Check whether user is in the area of attack point
   *
   * @param location user's current location
   * @return true if user in the circle of point, false for not
   */
  private boolean checkIfReachAttackPoint(BDLocation location) {
    double distance = DistanceUtil.getDistance(
        new LatLng(location.getLatitude(), location.getLongitude()),
        currentAttackPoint.getLatLng());
    return distance < Conf.POINT_RADIUS;
  }

  private void showAlertDialog(String message) {
    if (mAlertDialog == null) {
      mAlertDialog = new AlertDialog.Builder(getActivity())
          .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              VibrateUtils.cancel();
            }
          })
          .setMessage(message)
          .create();
    }
    mAlertDialog.show();
  }

  @OnClick(R.id.start_locate)
  void startLocateButtonClicked() {
    PermissionDispatcher.startLocateWithCheck(((BaseActivity) getActivity()));
  }

  @OnClick(R.id.start_animate)
  void startAnimateButtonClicked() {
    isReachedAttackPoint = true;
    onReachAttackPoint();
    RecordsUtils.dispatchTypeRecord(mRecordsHandler, initReachPointRecord(34.123123, 114.321321, currentAttackPoint.getId()));
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocateService() {
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

  protected abstract void initRecords();
}
