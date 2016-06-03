package com.lptiyu.tanke.gameplaying;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gamedata.GameDataActivity;
import com.lptiyu.tanke.gameplaying.assist.ConsoleHelper;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.GAME_ACTIVITY_FINISH_TYPE;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.gameplaying.task.GameTaskActivity;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.trace.tracing.ITracingHelper;
import com.lptiyu.tanke.trace.tracing.TracingCallback;
import com.lptiyu.tanke.trace.tracing.TracingHelper;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.VibrateUtils;
import com.lptiyu.tanke.widget.BaseSpotScrollView;

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
    BDLocationListener,
    TracingCallback,
    MapHelper.OnMapMarkerClickListener,
    BaseSpotScrollView.OnSpotItemClickListener {

  @BindView(R.id.map_view)
  TextureMapView mapView;

  boolean isReachedAttackPoint = false;
  boolean isGameFinished = false;

  long gameId;
  long lineId;
  long teamId;

  int currentAttackPointIndex = 0;
  Point currentAttackPoint;
  List<Point> mPoints;

  // used to create and dispatch record
  public static RecordsHandler mRecordsHandler;
  RunningRecord.Builder runningRecordBuilder;

  MapHelper mapHelper;
  LocateHelper locateHelper;
  ConsoleHelper consoleHelper;
  GameZipHelper gameZipHelper;
  ITracingHelper mTracingHelper;

  AlertDialog mAlertDialog;
  AlertDialog mLoadingDialog;

  static final long TEMP_GAME_ID = 1000000001L;
  static final long TEMP_LINE_ID = 2000000001L;
  static final long TEMP_TEAM_ID = 9000000001L;

  public GamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init(view);
  }

  private void init(View view) {

    showLoadingDialog();

    gameZipHelper = new GameZipHelper();

    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, TEMP_GAME_ID);
    lineId = intent.getLongExtra(Conf.LINE_ID, TEMP_LINE_ID);
    teamId = intent.getLongExtra(Conf.TEAM_ID, TEMP_TEAM_ID);

    if (!gameZipHelper.checkAndParseGameZip(gameId, lineId) || gameZipHelper.getmPoints().size() == 0) {
      ToastUtil.TextToast("游戏包加载失败");
      mLoadingDialog.dismiss();
      return;
      //TODO : notice user that the game zip is damaged, please download again,then finish this activity
    }
    //TODO : get game id and line id from intent
    ToastUtil.TextToast("游戏包加载完成");

    mPoints = gameZipHelper.getmPoints();

    mapHelper = new MapHelper(getActivity(), mapView);
    mapHelper.bindData(mPoints);
    mapHelper.setmMapMarkerClickListener(this);
    consoleHelper = new ConsoleHelper(getActivity(), view, mPoints);
    consoleHelper.setOnSpotClickListener(this);
    mTracingHelper = new TracingHelper(getActivity().getApplicationContext(), this);
    mTracingHelper.entityName(String.format("%d_%d".toLowerCase(), gameId, teamId));
    locateHelper = new LocateHelper(getActivity().getApplicationContext());
    locateHelper.registerLocationListener(this);

    mRecordsHandler = new RecordsHandler.Builder(gameId, teamId).build();
    runningRecordBuilder = new RunningRecord.Builder();

    initRecords();

//    mTracingHelper.start();
  }

  private RunningRecord initPointRecord(double x, double y, long pointId, RunningRecord.RECORD_TYPE type) {
    return runningRecordBuilder
        .x(x)
        .y(y)
        .pointId(pointId)
        .type(type)
        .createTime(System.currentTimeMillis())
        .build();
  }

  private RunningRecord initReachPointRecord(double x, double y, long pointId) {
    return initPointRecord(x, y, pointId, RunningRecord.RECORD_TYPE.POINT_REACH);
  }

  private RunningRecord initFinishPointRecord(double x, double y, long pointId) {
    return initPointRecord(x, y, pointId, RunningRecord.RECORD_TYPE.POINT_FINISH);
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

  /**
   * This method is to start timing task
   * when receive timing signal from GameTaskActivity
   */
  private void startTimingTaskFlow(Task timingTask) {
    if (timingTask == null) {
      return;
    }

  }


  /**
   * This method to notify user when they first arrive the attack point
   * vibrate and show the dialog to tell them click the nail in the map
   * then begin the tasks in this attack point
   */
  void onReachAttackPoint() {
    if (currentAttackPoint == null || currentAttackPointIndex < 0) {
      Timber.e("current attack point is error");
      return;
    }
    isReachedAttackPoint = true;
    mapHelper.onReachAttackPoint(currentAttackPointIndex);
    consoleHelper.onReachAttackPoint(currentAttackPointIndex);
  }


  void onNextPoint() {
    if (currentAttackPointIndex < mPoints.size() - 1) {
      currentAttackPointIndex++;
      currentAttackPoint = mPoints.get(currentAttackPointIndex);
      consoleHelper.updateNextPoint(currentAttackPointIndex);
      mapHelper.updateNextPoint(currentAttackPointIndex);
      isReachedAttackPoint = false;
    } else {
      //TODO : all point has arrive and the game is finished
      isGameFinished = true;
      ToastUtil.TextToast("您已经完成了所有的攻击点");
    }

  }

  void showAlertDialog(String message) {
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

  void showLoadingDialog() {
    if (mLoadingDialog == null) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
      TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
      textView.setText(getString(R.string.loading));
      mLoadingDialog = new AlertDialog.Builder(getActivity())
          .setCancelable(false)
          .setView(view)
          .create();
    }
    mLoadingDialog.show();
  }

  @OnClick(R.id.game_data)
  void startGameDataActivity() {
    Intent intent = new Intent();
    intent.setClass(getActivity(), GameDataActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.move_to_target)
  void moveToTarget() {
    mapHelper.animateCameraToCurrentTarget();
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

  @Override
  public void onReceiveLocation(BDLocation location) {
    mapHelper.onReceiveLocation(location);
    /**
     * Check whether user reach the attack point
     * when the game is not finished and the point is not reached
     */
    if (!isGameFinished && !isReachedAttackPoint) {
      if (checkIfReachAttackPoint(location)) {
        onReachAttackPoint();
        VibrateUtils.vibrate();
        showAlertDialog(getString(R.string.reach_attack_point));
        RecordsUtils.dispatchTypeRecord(mRecordsHandler, initReachPointRecord(location.getLatitude(), location.getLongitude(), currentAttackPoint.getId()));
      }
    }
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
    mapHelper.animateCameraToMarkerByIndex(position);
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocateService() {
    locateHelper.startLocate();
    mapHelper.animateCameraToCurrentPosition();
  }

  @Override
  public void onMarkerClicked(Point point) {
    if (currentAttackPoint == point) {
      if (!isReachedAttackPoint) {
        ToastUtil.TextToast("您还未到达该攻击点");
        return;
      }
    }
    Intent intent = new Intent();
    intent.setClass(getActivity(), GameTaskActivity.class);
    intent.putExtra(Conf.CLICKED_POINT, point);
    intent.putExtra(Conf.GAME_ID, gameId);
    intent.putExtra(Conf.TEAM_ID, teamId);
    startActivityForResult(intent, Conf.REQUEST_CODE_TASK_ACTIVITY);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Conf.REQUEST_CODE_TASK_ACTIVITY && resultCode == Conf.RESULT_CODE_TASK_ACTIVITY) {
      if (data != null) {
        GAME_ACTIVITY_FINISH_TYPE type = ((GAME_ACTIVITY_FINISH_TYPE) data.getSerializableExtra(Conf.GAME_ACTIVITY_FINISH_TYPE));
        switch (type) {

          case TIMING_TASK:
            Task task = data.getParcelableExtra(Conf.TIMING_TASK);
            startTimingTaskFlow(task);
            break;

          case USER_ACTION:
            int clickedPointIndex = data.getIntExtra(Conf.IS_POINT_TASK_ALL_FINISHED_INDEX, -1);
            if (clickedPointIndex != currentAttackPointIndex) {
              return;
            }

            boolean isAllTaskFinished = data.getBooleanExtra(Conf.IS_POINT_TASK_ALL_FINISHED, false);
            if (isAllTaskFinished) {
              RecordsUtils.dispatchTypeRecord(mRecordsHandler, initFinishPointRecord(34.123123, 114.321321, currentAttackPoint.getId()));
              onNextPoint();
            }
            break;
        }
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionDispatcher.onActivityRequestPermissionsResult(((BaseActivity) getActivity()), requestCode, permissions, grantResults);
  }

  @Override
  public void onTraceStart() {
    Timber.e("hawk eye service start");
  }

  @Override
  public void onTracePush(byte b, String s) {

  }

  @Override
  public void onTraceStop() {
    Timber.e("hawk eye service stop");
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
      locateHelper.unRegisterLocationListener(this);
    }
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
