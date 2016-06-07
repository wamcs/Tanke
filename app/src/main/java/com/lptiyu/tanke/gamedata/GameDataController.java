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
import com.lptiyu.tanke.gamedisplay.DummyData;
import com.lptiyu.tanke.gameplaying.pojo.Point;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.pojo.GameDataEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

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

  private List<Point> mPoints;
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
//    resumeGameData();
  }

  private void resumeGameData() {
    Intent intent = getIntent();
    mPoints = intent.getParcelableArrayListExtra(Conf.GAME_POINTS);
    mRecords = intent.getParcelableExtra(Conf.GAME_RECORDS);
    if (mPoints == null || mRecords == null) {
      resumeGameDataFromDisk();
    }
    resumeGameDataFromIntent();
  }

  private void resumeGameDataFromIntent() {

  }

  private void resumeGameDataFromDisk() {

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
    return Observable.just(DumGameData.entities);
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

  @OnClick(R.id.activity_game_data_toolbar_imageview_right)
  void share() {
    //TODO : share the game data
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    back();
  }

}
