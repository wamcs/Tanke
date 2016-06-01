package com.lptiyu.tanke.base.recyclerview;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.FragmentController;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/27
 *
 * @author ldx
 */
public abstract class BaseListFragmentController<Data> extends FragmentController {

  private int mListPage = 0;

  private Subscription lastRequest;

  private boolean isRefreshing  = false;

  public BaseListFragmentController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  public void refreshTop() {
    if (isRefreshing) {
      return;
    }
    mListPage = 0;
    changeRefreshState(true);
    lastRequest = requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            getAdapter().setData(datas);
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

  public void refreshBottom() {
    if (isRefreshing) {
      return;
    }
    mListPage++;
    changeRefreshState(true);
    lastRequest = requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            getAdapter().addData(datas);
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

  public boolean isRefreshing() {
    return isRefreshing;
  }

  private void changeRefreshState(boolean refreshing) {
    this.isRefreshing = refreshing;
    onRefreshStateChanged(refreshing);
  }

  protected void onRefreshStateChanged(boolean isRefreshing) {

  }

  protected void onError(Throwable t) {
    Timber.e(t, "Loading error...");
  }

  protected abstract Observable<List<Data>> requestData(int page);

  protected abstract <T extends BaseAdapter<Data>> T getAdapter();

}
