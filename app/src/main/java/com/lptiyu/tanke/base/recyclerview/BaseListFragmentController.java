package com.lptiyu.tanke.base.recyclerview;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;

import java.util.List;

import rx.Observable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/27
 *
 * @author ldx
 */
public abstract class BaseListFragmentController<Data> extends FragmentController implements BaseListControllerImpl.DataInteractionListener<Data> {

  BaseListControllerImpl<Data> impl;

  public BaseListFragmentController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
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
