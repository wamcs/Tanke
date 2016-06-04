package com.lptiyu.tanke.base.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;

import java.util.List;

import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/4
 *
 * @author ldx
 */
public abstract class BaseListActivityController<Data> extends ActivityController
    implements BaseListControllerImpl.DataInteractionListener<Data> {

  private BaseListControllerImpl<Data> impl;

  public BaseListActivityController(AppCompatActivity activity, View view) {
    super(activity, view);
    impl = new BaseListControllerImpl<>(this);
  }

  @Override
  public boolean isRefreshing() {
    return impl.isRefreshing();
  }

  @Override
  public abstract Observable<List<Data>> requestData(int page);

  @NonNull
  @Override
  public abstract BaseAdapter<Data> getAdapter();

  @Override
  public abstract void onRefreshStateChanged(boolean isRefreshing);

  @Override
  public abstract void onError(Throwable t);
}
