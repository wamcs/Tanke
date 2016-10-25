package com.lptiyu.tanke.base.recyclerview;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lptiyu.tanke.base.controller.ActivityController;

import java.util.List;

import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/16
 *
 * @author ldx
 */
public abstract class LasyListFragmentController<T> extends BaseListFragmentController<T> {

  BaseAdapter<T> adapter;

  RecyclerView recyclerView;

  SwipeRefreshLayout swipeRefreshLayout;

  public LasyListFragmentController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
    adapter = new BaseAdapter<T>() {
      @Override
      public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return LasyListFragmentController.this.onCreateViewHolder(parent, viewType);
      }
    };

    recyclerView = (RecyclerView) view.findViewById(getRecyclerViewResId());
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(getSwipeRefreshLayoutResId());
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshTop();
      }
    });
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(onCreateLayoutManager());
    refreshTop();
  }

  public abstract BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType);

  public abstract int getRecyclerViewResId();

  public abstract int getSwipeRefreshLayoutResId();

  public RecyclerView.LayoutManager onCreateLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  @Override
  public abstract Observable<List<T>> requestData(int page);

  @NonNull
  @Override
  public BaseAdapter<T> getAdapter() {
    return adapter;
  }

  @Override
  public void onRefreshStateChanged(boolean isRefreshing) {
    swipeRefreshLayout.setRefreshing(isRefreshing);
  }

  @Override
  public void onError(Throwable t) {
    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
  }
}
