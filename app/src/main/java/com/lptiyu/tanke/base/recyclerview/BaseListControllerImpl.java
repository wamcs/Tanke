package com.lptiyu.tanke.base.recyclerview;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/4
 *
 * @author ldx
 */
class BaseListControllerImpl<Data> implements ListController{

  private int mListPage = 0;

  private Subscription lastRequest;

  private boolean isRefreshing = false;

  private DataInteractionListener<Data> listener;

  public BaseListControllerImpl(DataInteractionListener<Data> listener) {
    if (listener == null) {
      throw new IllegalArgumentException("DataInteractionListener can not be null.");
    }
    this.listener = listener;
  }

  @Override
  public void refreshTop() {
    if (isRefreshing) {
      return;
    }
    mListPage = 0;
    changeRefreshState(true);
    lastRequest = listener.requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            listener.getAdapter().setData(datas);
            changeRefreshState(false);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            onError(throwable);
            changeRefreshState(false);
          }
        });
  }

  @Override
  public void refreshBottom() {
    if (isRefreshing) {
      return;
    }
    mListPage++;
    changeRefreshState(true);
    lastRequest = listener.requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            listener.getAdapter().addData(datas);
            changeRefreshState(false);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            onError(throwable);
            changeRefreshState(false);
          }
        });
  }

  @Override
  public boolean isRefreshing() {
    return isRefreshing;
  }

  private void changeRefreshState(boolean refreshing) {
    this.isRefreshing = refreshing;
    onRefreshStateChanged(refreshing);
  }

  private void onRefreshStateChanged(boolean isRefreshing) {
    listener.onRefreshStateChanged(isRefreshing);
  }

  private void onError(Throwable t) {
    Timber.e(t, "Loading error...");
    listener.onError(t);
  }

  public interface DataInteractionListener<T> {

    Observable<List<T>> requestData(int page);

    @NonNull
    BaseAdapter<T> getAdapter();

    void onRefreshStateChanged(boolean isRefreshing);

    void onError(Throwable t);

  }

}
