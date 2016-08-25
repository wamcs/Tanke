package com.lptiyu.tanke.gameplaying;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.bumptech.glide.Glide;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.gameplaying.assist.MapHelper;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameDetailsEntity;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.trace.history.HistoryTrackCallback;
import com.lptiyu.tanke.trace.history.HistoryTrackHelper;
import com.lptiyu.tanke.trace.history.IHistoryTrackHelper;
import com.lptiyu.tanke.utils.TimeUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;
import com.lptiyu.tanke.widget.CircularImageView;
import com.lptiyu.tanke.widget.CustomTextView;
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
  CustomTextView mToolbarTitle;
  @BindView(R.id.activity_game_share_map_view)
  TextureMapView mMapView;
  @BindView(R.id.layout_game_share_form_other_team_info)
  CustomTextView mFormTeamInfo;
  @BindView(R.id.layout_game_share_form_time)
  CustomTextView mFormTime;
  @BindView(R.id.layout_game_share_form_distance)
  CustomTextView mFormDistance;
  @BindView(R.id.layout_game_share_form_task_num)
  CustomTextView mFormTaskNum;
  @BindView(R.id.layout_game_share_form_complete_time)
  CustomTextView mFormCompleteTime;
  @BindView(R.id.layout_game_share_form_get_exp)
  CustomTextView mFormGetExp;
  @BindView(R.id.layout_game_share_user_avatar)
  CircularImageView mUserAvatar;
  @BindView(R.id.layout_game_share_form_nickname)
  CustomTextView mNickName;

  private long gameId;
  private long teamId;
  private List<Point> mPoints;
  private GameDetailsEntity mGameDetailsEntity;

  private long startTimeSecond;
  private long endTimeSecond;
  private int consumeTimeMinute;
  private int totalTaskNum;
  private double totalDistance;
  private int totalExp;

  private PolylineOptions polyline;
  private MapStatusUpdate msUpdate;


  private MapHelper mapHelper;
  private BaiduMap mMap;
  private IHistoryTrackHelper mTrackHelper;

  private AlertDialog mLoadingDialog;

  public GameShareController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, Conf.TEMP_GAME_ID);
    mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
    mPoints = intent.getParcelableArrayListExtra(Conf.GAME_POINTS);
    teamId = intent.getLongExtra(Conf.TEAM_ID, Conf.TEMP_TEAM_ID);
    checkAndResumeGameData();
    init();
  }

  private void checkAndResumeGameData() {
    if (mPoints == null) {
      if (gameId != Long.MIN_VALUE) {
        loadGameDataFromDisk();
      }
    }
    if (mPoints == null) {
      //TODO : the game zip is not exist
      finish();
    }
  }

  private void loadGameDataFromDisk() {
    GameZipHelper zipHelper = new GameZipHelper();
    if (!zipHelper.checkAndParseGameZip(gameId) || zipHelper.getmPoints().size() == 0) {
      return;
    }
    mPoints = zipHelper.getmPoints();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.game_share));
    showLoadingDialog();
    initMap();
    mTrackHelper = new HistoryTrackHelper(getContext(), this);
    mMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
      @Override
      public void onMapLoaded() {
        resumeFromRecords();
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
    if (RecordsUtils.isGameStartedFromDisk(gameId) && RecordsUtils.getmRecordsHandler() != null) {
      RecordsUtils.getmRecordsHandler().dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
        @Override
        public void dataResumed(List<RunningRecord> recordList) {
          if (recordList == null) {
            Timber.e("Resume from history records error");
            mLoadingDialog.dismiss();
            return;
          }
          resumePointRecords(recordList);
          Glide.with(getActivity()).load(Accounts.getAvatar()).error(R.mipmap.default_avatar).into(mUserAvatar);
          mNickName.setText(Accounts.getNickName());
          consumeTimeMinute = ((int) (endTimeSecond - startTimeSecond) / 60);
          mFormTime.setText(String.valueOf(consumeTimeMinute));
          mFormCompleteTime.setText(TimeUtils.getDateTimeWithoutYear(endTimeSecond * 1000));
          mFormTaskNum.setText(String.valueOf(totalTaskNum));
          mFormGetExp.setText(String.valueOf(totalExp));
          HttpService.getGameService().getGameFinishedNum(gameId)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Action1<Response<Integer>>() {
                @Override
                public void call(Response<Integer> integerResponse) {
                  mFormTeamInfo.setText(String.format(getString(R.string.game_share_form_game_finished_num_formatter), integerResponse.getData()));
                }
              }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                  mFormTeamInfo.setText(String.format(getString(R.string.game_share_form_game_finished_num_formatter), 0));
                }
              });
          mTrackHelper.queryHistoryTrack(
              Conf.makeUpTrackEntityName(gameId, Accounts.getId()),
              (int)(startTimeSecond),
              (int)(endTimeSecond));
        }
      });
    }
  }

  private void resumePointRecords(List<RunningRecord> recordList) {
    for (RunningRecord record : recordList) {

      RunningRecord.RECORD_TYPE type = record.getState();

      switch (type) {
        case GAME_START:
          startTimeSecond = record.getCreateTime();
          totalTaskNum += 1;
          startPoint(record);
          finishPoint(record);
          break;

        case POINT_REACH:
          startPoint(record);
          break;

        case TASK_START:

          break;

        case TASK_FINISH:
          totalTaskNum += 1;
          break;

        case POINT_FINISH:
          finishPoint(record);
          break;

        case GAME_FINISH:
          endTimeSecond = record.getCreateTime();
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

  private void startPoint(RunningRecord record) {
    mapHelper.updateNextPoint(getIndexByPointId(record.getPointId()));
  }

  private void finishPoint(RunningRecord record) {
    int index = getIndexByPointId(record.getPointId());
    mapHelper.onReachAttackPoint(index);
    if (index >= 0 && mPoints != null && index < mPoints.size()) {
      totalExp += getPointTotalExp(mPoints.get(index));
    }
  }

  private void showLoadingDialog() {
    if (mLoadingDialog == null) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
      CustomTextView textView = ((CustomTextView) view.findViewById(R.id.loading_dialog_textview));
      textView.setText(getString(R.string.loading));
      mLoadingDialog = new AlertDialog.Builder(getActivity())
          .setView(view)
          .create();
    }
    mLoadingDialog.show();
  }

  private void drawHistoryTrack(final List<LatLng> points) {
    if (points == null || points.size() == 0) {

    } else if (points.size() > 1) {
      polyline = new PolylineOptions().width(10)
          .color(Color.RED).points(points);
      mMap.addOverlay(polyline);
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
    List<LatLng> latLngList = new ArrayList<>();
    if (historyTrackData != null && historyTrackData.getStatus() == 0) {
      List<LatLng> points = historyTrackData.getListPoints();
      if (points != null && mPoints.size() > 0) {
        latLngList.addAll(points);
        totalDistance = historyTrackData.distance / 1000;
        drawHistoryTrack(latLngList);
        thread.mainThread(new Runnable() {
          @Override
          public void run() {
            mFormDistance.setText(String.format("%2.1f".toLowerCase(), totalDistance));
            mLoadingDialog.dismiss();
          }
        });
      } else {
        thread.mainThread(new Runnable() {
          @Override
          public void run() {
            mFormDistance.setText(String.valueOf(0));
            ToastUtil.TextToast("无历史轨迹");
            mLoadingDialog.dismiss();
          }
        });
      }
    } else {
      thread.mainThread(new Runnable() {
        @Override
        public void run() {
          mFormDistance.setText(String.valueOf(0));
          ToastUtil.TextToast("无历史轨迹");
          mLoadingDialog.dismiss();
        }
      });
    }
    thread.mainThread(new Runnable() {
      @Override
      public void run() {
        animateToPointsBounds();
      }
    });
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
    HttpService.getGameService().getShareUrl(Accounts.getId(), Accounts.getToken(), gameId, teamId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Action1<Response<String>>() {
          @Override
          public void call(Response<String> stringResponse) {
            String SHARE_TITLE = String.format("我%s完成了%s，快和我一起来探秘吧".toLowerCase(), mGameDetailsEntity.getTitle());
            String SHARE_CONTENT = String.format("探秘耗时%d分钟\n完成了%d个任务\n跋山涉水%s公里".toLowerCase(),
                consumeTimeMinute,
                totalTaskNum,
                String.format("%2.1f".toLowerCase(), totalDistance));
            Intent intent = new Intent(getContext(),ShareDialog.class);
            intent.putExtra(Conf.SHARE_TITLE,SHARE_TITLE);
            intent.putExtra(Conf.SHARE_TEXT,SHARE_CONTENT);
            intent.putExtra(Conf.SHARE_IMG_URL,mGameDetailsEntity.getImg());
            intent.putExtra(Conf.SHARE_URL,stringResponse.getData());
            startActivity(intent);
            overridePendingTransition(R.anim.translate_in_bottom,R.anim.translate_out_bottom);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            ToastUtil.TextToast("分享失败");
          }
        });
  }

}
