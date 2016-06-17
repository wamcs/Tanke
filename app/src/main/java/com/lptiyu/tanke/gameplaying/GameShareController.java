package com.lptiyu.tanke.gameplaying;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.trace.history.HistoryTrackCallback;
import com.lptiyu.tanke.trace.history.HistoryTrackHelper;
import com.lptiyu.tanke.trace.history.IHistoryTrackHelper;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.widget.dialog.ShareDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-13
 *         email: wonderfulifeel@gmail.com
 */
public class GameShareController extends ActivityController implements
    HistoryTrackCallback {

  @BindView(R.id.tool_bar)
  Toolbar mToolbar;
  @BindView(R.id.activity_game_share_form_root)
  RelativeLayout mFormRoot;
  @BindView(R.id.activity_game_share_toolbar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.activity_game_share_map_view)
  TextureMapView mMapView;
  @BindView(R.id.layout_game_share_form_time)
  TextView mFormTime;
  @BindView(R.id.layout_game_share_form_speed)
  TextView mFormSpeed;
  @BindView(R.id.layout_game_share_form_calorie)
  TextView mFormCalorie;
  @BindView(R.id.layout_game_share_form_exp)
  TextView mFormExp;
  @BindView(R.id.layout_game_share_form_distance)
  TextView mFormDistance;

  private long gameId;
  private long teamId;
  private List<Point> mPoints;

  private long startTimeMillis;
  private long endTimeMillis;
  private int totalDistance;
  private int totalExp;

  private PolylineOptions polyline;
  private MapStatusUpdate msUpdate;

  private MapHelper mapHelper;
  private BaiduMap mMap;
  private IHistoryTrackHelper mTrackHelper;

  private ShareDialog shareDialog;
  private AlertDialog mLoadingDialog;

  public GameShareController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
    teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
    mPoints = intent.getParcelableArrayListExtra(Conf.GAME_POINTS);
    init();
  }

  private void init() {
    Timber.e("1");
    mToolbarTitle.setText(getString(R.string.game_share));
    showLoadingDialog();
    initMap();
    mTrackHelper = new HistoryTrackHelper(getContext(), this);
    mMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
      @Override
      public void onMapLoaded() {
        Timber.e("2");
        mTrackHelper.queryHistoryTrack(Conf.makeUpTrackEntityName(gameId, teamId));
      }
    });
  }

  private void initMap() {
    mapHelper = new MapHelper(getContext(), mMapView);
    mapHelper.bindData(mPoints);
    mapHelper.initMapFlow();
    mMap = mapHelper.getmBaiduMap();
  }

  private void resumeFromRecords() {
    Timber.e("9");
    if (RecordsUtils.isGameStartedFromDisk(gameId) && RecordsUtils.getmRecordsHandler() != null) {
      Timber.e("10");
      RecordsUtils.getmRecordsHandler().dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
        @Override
        public void dataResumed(List<RunningRecord> recordList) {
          if (recordList == null) {
            Timber.e("Resume from history records error");
            mLoadingDialog.dismiss();
            return;
          }
          resumePointRecords(recordList);
          mFormTime.setText(TimeUtils.getFriendlyTime(endTimeMillis - startTimeMillis));
          mFormSpeed.setText(String.valueOf(totalDistance / ((endTimeMillis - startTimeMillis) / TimeUtils.ONE_MINUTE_TIME)));
          mFormCalorie.setText("364");
          mFormExp.setText(String.valueOf(totalExp));
          mFormDistance.setText(String.valueOf(totalDistance));
          mLoadingDialog.dismiss();
        }
      });
    } else {
      mLoadingDialog.dismiss();
    }
  }

  private void resumePointRecords(List<RunningRecord> recordList) {
    for (RunningRecord record : recordList) {
      RunningRecord.RECORD_TYPE type = record.getType();
      switch (type) {
        case GAME_START:
          startTimeMillis = record.getCreateTime();
          break;

        case POINT_REACH:
          mapHelper.updateNextPoint(getIndexByPointId(record.getPointId()));
          break;

        case TASK_START:

          break;

        case TASK_FINISH:

          break;

        case POINT_FINISH:
          int index = getIndexByPointId(record.getPointId());
          mapHelper.onReachAttackPoint(index);
          if (index >= 0 && mPoints != null && index < mPoints.size()) {
            totalExp += getPointTotalExp(mPoints.get(index));
          }
          break;

        case GAME_FINISH:
          endTimeMillis = record.getCreateTime();
          break;
      }
    }
  }

  private int getPointTotalExp(Point point) {
    int result = 0;
    Set<Map.Entry<String, Task>> entries = point.getTaskMap().entrySet();
    for (Map.Entry<String, Task> entry : entries) {
      Task task = entry.getValue();
      if (task != null) {
        result += task.getExp();
      }
    }
    return result;
  }

  private int getIndexByPointId(long pointId) {
    if (mPoints == null) {
      return -1;
    }
    for (Point p : mPoints) {
      if (p.getId() == pointId) {
        return p.getPointIndex();
      }
    }
    return -1;
  }

  private void showLoadingDialog() {
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

  private void drawHistoryTrack(final List<LatLng> points, double distance) {
    if (points == null || points.size() == 0) {
    } else if (points.size() > 1) {
      polyline = new PolylineOptions().width(10)
          .color(Color.RED).points(points);
      totalDistance = (int) distance;
      mMap.addOverlay(polyline);
      animateToPointsBounds();
    }
  }

  private void animateToPointsBounds() {
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (Point point : mPoints) {
      builder.include(point.getLatLng());
    }
    msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
    mMap.setMapStatus(msUpdate);
  }

  @Override
  public void onQueryHistoryTrackCallback(HistoryTrackData historyTrackData) {
    Timber.e("3");
    List<LatLng> latLngList = new ArrayList<>();
    if (historyTrackData != null && historyTrackData.getStatus() == 0) {
      Timber.e("4");
      if (historyTrackData.getListPoints() != null) {
        Timber.e("5");
        latLngList.addAll(historyTrackData.getListPoints());
      } else {
        Timber.e("6");
      }
      // 绘制历史轨迹
      Timber.e("7");
      drawHistoryTrack(latLngList, historyTrackData.distance);
    } else {
      ToastUtil.TextToast("无历史轨迹");
    }
    Timber.e("8");
    resumeFromRecords();
  }

  @Override
  public void onRequestFailedCallback(String s) {
    Timber.e(s);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mMapView != null) {
      mMapView.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mMapView != null) {
      mMapView.onPause();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mTrackHelper != null) {
      mTrackHelper.onDestroy();
    }
    if (mMapView != null) {
      mMapView.onDestroy();
    }
  }

  @Override
  public boolean onBackPressed() {
    onBackButtonClicked();
    return true;
  }

  @OnClick(R.id.activity_game_share_toolbar_imageview_left)
  public void onBackButtonClicked() {
    finish();
  }

  @OnClick(R.id.activity_game_share_toolbar_imageview_right)
  public void onShareButtonClicked() {

    HttpService.getGameService().getShareUrl(1, "11", gameId, teamId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<Response<String>>() {
          @Override
          public void call(Response<String> stringResponse) {
            String SHARE_TITLE = "测试标题";
            String SHARE_CONTENT = "测试内容";
            if (null == shareDialog) {
              shareDialog = new ShareDialog(getContext());
              shareDialog.setShareContent(SHARE_TITLE, SHARE_CONTENT, null, stringResponse.getData());
            }
            shareDialog.show();
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast("分享失败");
          }
        });

//    showLoadingDialog();
//    ScreenUtils.captureScreen(mToolbar, mFormRoot, mMapView, new ScreenUtils.ScreenCallBack() {
//      @Override
//      public void onScreenShot(String filePath) {
//        mLoadingDialog.dismiss();
//        if (null == filePath) {
//          Timber.d("capture screen error");
//          return;
//        }
//        String SHARE_TITLE = "测试标题";
//        String SHARE_CONTENT = "测试内容";
////        String SHARE_URL = "http://www.baidu.com";
//        if (null == shareDialog) {
//          shareDialog = new ShareDialog(getContext());
//          shareDialog.setShareContent(SHARE_TITLE, SHARE_CONTENT, filePath, null);
//          Timber.e(filePath);
//        }
//        shareDialog.show();
//      }
//    });
  }

}
