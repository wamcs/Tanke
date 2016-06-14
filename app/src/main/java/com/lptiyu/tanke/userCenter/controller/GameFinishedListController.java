package com.lptiyu.tanke.userCenter.controller;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.io.net.Response;
import com.lptiyu.tanke.pojo.GameFinishedEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class GameFinishedListController extends BaseListActivityController<GameFinishedEntity> {

    @BindView(R.id.default_tool_bar_textview)
    TextView mTitle;

    @BindView(R.id.GameListRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.GameListRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    public GameFinishedListController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        mTitle.setText("已完成的");
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
                    return response.getData();
                }
            });
    }

    @NonNull
    @Override
    public BaseAdapter<GameFinishedEntity> getAdapter() {
        return null;
    }

    @Override
    public void onRefreshStateChanged(boolean isRefreshing) {

    }

    @Override
    public void onError(Throwable t) {

    }
}
