package com.lptiyu.tanke.userCenter.controller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.pojo.GamePlayingEntity;

import java.util.List;

import rx.Observable;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class GamePlayingListController extends BaseListActivityController<GamePlayingEntity> {


    public GamePlayingListController(AppCompatActivity activity, View view) {
        super(activity, view);
    }

    @Override
    public Observable<List<GamePlayingEntity>> requestData(int page) {
        return null;
    }

    @NonNull
    @Override
    public BaseAdapter<GamePlayingEntity> getAdapter() {
        return null;
    }

    @Override
    public void onRefreshStateChanged(boolean isRefreshing) {

    }

    @Override
    public void onError(Throwable t) {

    }
}
