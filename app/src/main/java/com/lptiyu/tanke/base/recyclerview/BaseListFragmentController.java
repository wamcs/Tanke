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
    mListPage = 0;

    if (lastRequest != null && isRefreshing() && !lastRequest.isUnsubscribed()) {
      lastRequest.unsubscribe();
    }
    showRefreshState(true);
    lastRequest = requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            getAdapter().setData(datas);
            showRefreshState(false);
          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            showRefreshState(false);
          }
        });
  }

  public void refreshBottom() {
    mListPage++;
    if (lastRequest != null && isRefreshing() && !lastRequest.isUnsubscribed()) {
      lastRequest.unsubscribe();
    }
    showRefreshState(true);
    lastRequest = requestData(mListPage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Data>>() {
          @Override
          public void call(List<Data> datas) {
            getAdapter().addData(datas);
            showRefreshState(false);
          }
        });
  }

  public boolean isRefreshing() {
    return isRefreshing;
  }

  public void showRefreshState(boolean refreshing) {
    this.isRefreshing = refreshing;
  }

  public void showError(Throwable t) {
    Timber.e(t, "Loading error...");
  }

  public abstract Observable<List<Data>> requestData(int page);

  public abstract <T extends BaseAdapter<? extends RecyclerView.ViewHolder, Data>> T getAdapter();

}
