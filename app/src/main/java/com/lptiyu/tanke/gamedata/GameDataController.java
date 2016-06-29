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
import com.lptiyu.tanke.pojo.GameDataFinishEntity;
import com.lptiyu.tanke.pojo.GameDataNormalEntity;
import com.lptiyu.tanke.pojo.GameDataStartEntity;
import com.lptiyu.tanke.pojo.GameDetailsEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;
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
  private GameDetailsEntity mGameDetailsEntity;

  private GameDataNormalEntity.Builder gameDataEntityBuilder;

  private GameDataStartEntity gameDataStartEntity;
  private GameDataFinishEntity gameDataFinishEntity;
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
    mGameDetailsEntity = intent.getParcelableExtra(Conf.GAME_DETAIL);
    gameDataEntities = new ArrayList<>();
    gameDataEntityBuilder = new GameDataNormalEntity.Builder();
    mPoints = intent.getParcelableArrayListExtra(Conf.GAME_POINTS);
    mRecords = intent.getParcelableArrayListExtra(Conf.GAME_RECORDS);
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
    RunningRecord taskStartRecord = null;
    RunningRecord taskEndRecord = null;
    long gameStartTime = 0L;
    int totalExp = 0;
    for (RunningRecord record : mRecords) {
      RunningRecord.RECORD_TYPE type = record.getState();
      if (type == RunningRecord.RECORD_TYPE.POINT_REACH
          || type == RunningRecord.RECORD_TYPE.POINT_FINISH) {
        continue;
      }

      switch (type) {
        case GAME_START:
          gameDataStartEntity = new GameDataStartEntity();
          gameStartTime = record.getCreateTime();
          gameDataStartEntity.setGameId(gameId);
          gameDataStartEntity.setStartTime(record.getCreateTime());
          if (mPoints != null) {
            Point p = mPoints.get(0);
            List<String> taskIds = p.getTaskId();
            if (taskIds != null) {
              Map<String, Task> map = p.getTaskMap();
              if (map != null) {
                Task task = map.get(taskIds.get(0));
                if (task != null) {
                  totalExp += task.getExp();
                }
              }
            }
          }
          break;

        case TASK_START:
          taskStartRecord = record;
          break;

        case TASK_FINISH:
          taskEndRecord = record;
          if (taskStartRecord == null) {
            return;
          }
          if (taskEndRecord.getPointId() != taskStartRecord.getPointId()) {
            Timber.e("there is an error in running records");
            return;
          }
          Point p = pointMap.get(taskEndRecord.getPointId());
          if (p != null) {
            Map<String, Task> map = p.getTaskMap();
            if (map != null) {
              Task task = map.get(String.valueOf(taskEndRecord.getTaskId()));
              if (task != null) {
                gameDataEntityBuilder
                    .taskId(task.getId())
                    .taskName(task.getTaskName())
                    .type(task.getType())
                    .completePersonNum(0)
                    .completeTime(taskEndRecord.getCreateTime())
                    .completeComsumingTime(taskEndRecord.getCreateTime() - taskStartRecord.getCreateTime())
                    .exp(task.getExp());
                gameDataEntities.add(gameDataEntityBuilder.build());
                totalExp += task.getExp();
              }
            }
          }
          break;

        case GAME_FINISH:
          gameDataFinishEntity = new GameDataFinishEntity();
          gameDataFinishEntity.setCompleteTime(record.getCreateTime());
          gameDataFinishEntity.setConsumingTime(record.getCreateTime() - gameStartTime);
          break;
      }
    }
    if (gameDataFinishEntity != null) {
      gameDataFinishEntity.setTotalExp(totalExp);
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
    return Observable.just(gameId)
        .map(new Func1<Long, List<GameDataEntity>>() {

          @Override
          public List<GameDataEntity> call(Long aLong) {
            if (mPoints == null || mRecords == null) {
              if (gameId != Long.MIN_VALUE) {
                loadGameDataFromDisk();
              }
            }
            if (mPoints == null || mRecords == null) {
              return new ArrayList<>();
            }
            for (Point p : mPoints) {
              pointMap.put(p.getId(), p);
            }
            resumeGameData();
            gameDataStartEntity.setGameImage(mGameDetailsEntity.getImg());
            gameDataStartEntity.setGameLoc(mGameDetailsEntity.getArea());
            gameDataStartEntity.setGameTitle(mGameDetailsEntity.getTitle());
            mAdapter.bindStartEntityAndFinishEntity(gameDataStartEntity, gameDataFinishEntity);
            return gameDataEntities;
          }
        });
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
