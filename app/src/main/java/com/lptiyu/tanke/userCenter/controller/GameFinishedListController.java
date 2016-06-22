package com.lptiyu.tanke.userCenter.controller;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseViewHolder;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameFinishedEntity;
import com.lptiyu.tanke.userCenter.viewholder.UserGameFinishedHolder;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.thread;
import com.lptiyu.tanke.widget.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class GameFinishedListController extends BaseListActivityController<GameFinishedEntity> {

  @BindView(R.id.default_tool_bar_textview)
  CustomTextView mTitle;
  @BindView(R.id.no_data_imageview)
  ImageView mNoDataImage;

  @BindView(R.id.GameListRecyclerView)
  RecyclerView recyclerView;

  @BindView(R.id.GameListRefreshLayout)
  SwipeRefreshLayout swipeRefreshLayout;


  BaseAdapter<GameFinishedEntity> adapter = new BaseAdapter<GameFinishedEntity>() {
    @Override
    public BaseViewHolder<GameFinishedEntity> onCreateViewHolder(ViewGroup parent, int viewType) {
      return new UserGameFinishedHolder(parent);
    }
  };

  public GameFinishedListController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshTop();
      }
    });
    mTitle.setText("已完成的");
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(adapter);
    refreshTop();
  }

  @Override
  public Observable<List<GameFinishedEntity>> requestData(int page) {
    return HttpService.getUserService().gameFinished(Accounts.getId(), Accounts.getToken(), page)
        .map(new Func1<Response<List<GameFinishedEntity>>, List<GameFinishedEntity>>() {
          @Override
          public List<GameFinishedEntity> call(Response<List<GameFinishedEntity>> response) {
            if (response.getStatus() != Response.RESPONSE_OK) {
              throw new RuntimeException(response.getInfo());
            }
            List<GameFinishedEntity> result = response.getData();
            if (result.size() == 0) {
              if (mNoDataImage != null) {
                thread.mainThread(new Runnable() {
                  @Override
                  public void run() {
                    mNoDataImage.setVisibility(View.VISIBLE);
                  }
                });
              }
            } else {
              if (mNoDataImage != null) {
                thread.mainThread(new Runnable() {
                  @Override
                  public void run() {
                    mNoDataImage.setVisibility(View.GONE);
                  }
                });
              }
            }
            return result;
          }
        });
  }

  @OnClick(R.id.default_tool_bar_imageview)
  public void default_tool_bar_imageview() {
    finish();
  }

  @NonNull
  @Override
  public BaseAdapter<GameFinishedEntity> getAdapter() {
    return adapter;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {
    swipeRefreshLayout.setRefreshing(isRefreshing);
  }

  @Override
  public void onError(Throwable t) {
    ToastUtil.TextToast(t.getMessage());
  }

}
