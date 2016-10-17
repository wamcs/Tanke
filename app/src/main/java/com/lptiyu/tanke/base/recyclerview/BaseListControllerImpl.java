package com.lptiyu.tanke.base.recyclerview;

import android.support.annotation.NonNull;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ToastUtil;

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
class BaseListControllerImpl<Data> implements ListController {
    private Data data;
    private int mListPage = INIT_PAGE;
    private boolean m_need_load_more = true;//是否需要加载更多

    private Subscription lastRequest;

    private boolean isRefreshing = false;

    private DataInteractionListener<Data> listener;

    private static final int INIT_PAGE = 1;

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
        mListPage = INIT_PAGE;
        m_need_load_more = true;
        changeRefreshState(true);
        lastRequest = listener.requestData(mListPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Data>>() {
                    @Override
                    public void call(List<Data> datas) {

                        listener.getAdapter().setData(datas);
                        //                        RunApplication.gameList = (List<GameDisplayEntity>) listener
                        // .getAdapter().getData();
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

        if (!m_need_load_more)//不需要加载
        {
            changeRefreshState(false);
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
                        if (datas.size() == 0) {
                            m_need_load_more = false;
                            mListPage--;
                        }
                        listener.getAdapter().addData(datas);
                        changeRefreshState(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        m_need_load_more = true;
                        mListPage--;
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
        onRefreshStateChanged(refreshing);
        this.isRefreshing = refreshing;
    }

    private void onRefreshStateChanged(boolean isRefreshing) {
        if (listener != null) {
            listener.onRefreshStateChanged(isRefreshing);
        }
    }

    private void onError(Throwable t) {
        if (!NetworkUtil.checkIsNetworkConnected()) {
            ToastUtil.TextToast(R.string.no_network);
            return;
        }
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
