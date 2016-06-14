package com.lptiyu.tanke.userCenter.controller;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.pojo.GamePlayingEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class GamePlayingListController extends BaseListActivityController<GamePlayingEntity> implements
        SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.default_tool_bar_textview)
    TextView mTitle;
    @BindView(R.id.GameListRecyclerView)
    RecyclerView GameListRecyclerView;
    @BindView(R.id.GameListRefreshLayout)
    SwipeRefreshLayout GameListRefreshLayout;

    private List<GamePlayingEntity> entities;

    public GamePlayingListController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
    }

    private void init(){
        mTitle.setText("正在进行");
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

    @OnClick(R.id.default_tool_bar_imageview)
    void back(){
        finish();
    }

    @Override
    public void onRefresh() {

    }
}
