package com.lptiyu.tanke.gamedata;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.gameplaying.assist.zip.GameZipHelper;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.MemRecords;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDataEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-2
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataController extends BaseListActivityController<GameDataEntity> implements
    SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.activity_game_data_toolbar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.swipe_refresh_layout)
  SwipeRefreshLayout mRefreshLayout;
  @BindView(R.id.recycler_view)
  RecyclerView mRecyclerView;

  private long gameId;

  private GameDataEntity.Builder gameDataEntityBuilder;

  private List<GameDataEntity> gameDataEntities;
  private List<Point> mPoints;
  private Map<Long, Point> pointMap;
  private List<RunningRecord> mRecords;

  private GameDataAdapter mAdapter;

  public GameDataController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.game_data));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new GameDataAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRefreshLayout.setOnRefreshListener(this);
    pointMap = new HashMap<>();
    checkAndResumeGameData();
    onRefresh();
  }

  private void checkAndResumeGameData() {
    Intent intent = getIntent();
    gameId = intent.getLongExtra(Conf.GAME_ID, Long.MIN_VALUE);
    gameDataEntities = new ArrayList<>();
    gameDataEntityBuilder = new GameDataEntity.Builder();
    mPoints = intent.getParcelableArrayListExtra(Conf.GAME_POINTS);
    mRecords = intent.getParcelableArrayListExtra(Conf.GAME_RECORDS);
    if (mPoints == null || mRecords == null) {
      if (gameId != Long.MIN_VALUE) {
        loadGameDataFromDisk();
      }
    }
    if (mPoints == null || mRecords == null) {
      return;
    }
    for (Point p : mPoints) {
      pointMap.put(p.getId(), p);
    }
    resumeGameData();
  }

  private void loadGameDataFromDisk() {
    GameZipHelper zipHelper = new GameZipHelper();
    if (!zipHelper.checkAndParseGameZip(gameId) || zipHelper.getmPoints().size() == 0) {
      return;
    }
    mPoints = zipHelper.getmPoints();
    RecordsHandler handler = RecordsUtils.getmRecordsHandler();
    if (handler != null) {
      MemRecords memRecords = handler.getMemRecords();
      if (memRecords != null) {
        mRecords = memRecords.getAll();
      }
    }
  }

  private void resumeGameData() {
    RunningRecord startRecord = null;
    RunningRecord endRecord = null;
    for (RunningRecord record : mRecords) {
      if (record.getState() != RunningRecord.RECORD_TYPE.TASK_START && record.getState() != RunningRecord.RECORD_TYPE.TASK_FINISH) {
        continue;
      }
      if (record.getState() == RunningRecord.RECORD_TYPE.TASK_START) {
        startRecord = record;
      } else {
        endRecord = record;
        if (startRecord == null) {
          return;
        }
        if (endRecord.getPointId() != startRecord.getPointId()) {
          Timber.e("there is an error in running records");
          return;
        }
        Point p = pointMap.get(endRecord.getPointId());
        if (p != null) {
          Map<String, Task> map = p.getTaskMap();
          if (map != null) {
            Task task = map.get(String.valueOf(endRecord.getTaskId()));
            if (task != null) {
              gameDataEntityBuilder
                  .taskId(task.getId())
                  .taskName(task.getTaskName())
                  .type(task.getType())
                  .completePersonNum(0)
                  .completeTime(endRecord.getCreateTime())
                  .completeComsumingTime(endRecord.getCreateTime() - startRecord.getCreateTime())
                  .exp(task.getExp());
              gameDataEntities.add(gameDataEntityBuilder.build());
            }
          }
        }
      }
    }
  }

  @Override
  public void onRefresh() {
    if (!isRefreshing()) {
      refreshTop();
    }
  }

  @Override
  public Observable<List<GameDataEntity>> requestData(int page) {
    //TODO : reload records and points info from disk "resumeGameDataFromDisk()"
    return Observable.just(gameDataEntities);
  }

  @NonNull
  @Override
  public BaseAdapter<GameDataEntity> getAdapter() {
    return mAdapter;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {
    if (mRefreshLayout != null) {
      mRefreshLayout.setRefreshing(isRefreshing);
    }
  }

  @Override
  public void onError(Throwable t) {

  }

  @OnClick(R.id.activity_game_data_toolbar_imageview_left)
  void back() {
    finish();
  }

}
