package com.lptiyu.tanke.gamedata;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.gamedisplay.ElasticItemDecoration;
import com.lptiyu.tanke.gamedisplay.ElasticTouchListener;
import com.lptiyu.tanke.gamedisplay.GameDisplayAdapter;

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
public class GameDataController extends BaseListActivityController<GameDataEntity> {

  @BindView(R.id.default_tool_bar_textview)
  TextView mToolbarTitle;
  @BindView(R.id.recycler_view)
  RecyclerView mRecyclerView;

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
    ElasticTouchListener listener = new ElasticTouchListener();
    listener.setOnRefreshListener(new ElasticTouchListener.OnRefreshListener() {
      @Override
      public void onRefresh() {
        if (!isRefreshing()) {
          refreshTop();
        }
      }
    });
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addItemDecoration(new ElasticItemDecoration(getContext()));
  }

  @Override
  public Observable<List<GameDataEntity>> requestData(int page) {
    return null;
  }

  @NonNull
  @Override
  public BaseAdapter<GameDataEntity> getAdapter() {
    return null;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {

  }

  @Override
  public void onError(Throwable t) {

  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

  @Override
  public boolean onBackPressed() {
    super.onBackPressed();
    back();
    return true;
  }

}
