package com.lptiyu.tanke.userCenter.controller;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.recyclerview.BaseAdapter;
import com.lptiyu.tanke.base.recyclerview.BaseListActivityController;
import com.lptiyu.tanke.entity.eventbus.LeaveGame;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.net.HttpService;
import com.lptiyu.tanke.net.Response;
import com.lptiyu.tanke.pojo.GamePlayingEntity;
import com.lptiyu.tanke.userCenter.adapter.GamePlayingAdapter;
import com.lptiyu.tanke.utils.ThreadUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Func1;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class GamePlayingListController extends BaseListActivityController<GamePlayingEntity> implements
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.default_tool_bar_textview)
    TextView mTitle;
    @BindView(R.id.no_data_imageview)
    ImageView mNoDataImage;

    @BindView(R.id.GameListRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.GameListRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager mLayoutManager;
    GamePlayingAdapter adapter = new GamePlayingAdapter();

    public GamePlayingListController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        init();
        EventBus.getDefault().register(this);
    }

    private void init() {
        mTitle.setText("正在进行的");
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    refreshBottom();
                }
            }
        });

        refreshTop();
    }


    @Override
    public Observable<List<GamePlayingEntity>> requestData(int page) {
        return HttpService.getUserService()
                .gamePlaying(Accounts.getId(), Accounts.getToken(), page)
                .map(new Func1<Response<List<GamePlayingEntity>>, List<GamePlayingEntity>>() {
                    @Override
                    public List<GamePlayingEntity> call(Response<List<GamePlayingEntity>> response) {
                        if (response.getStatus() != Response.RESPONSE_OK) {
                            throw new RuntimeException(response.getInfo());
                        }
                        List<GamePlayingEntity> result = response.getData();
                        if (result.size() == 0) {
                            if (mNoDataImage != null && mLayoutManager.getItemCount() <= 0) {
                                ThreadUtils.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImage.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } else {
                            if (mNoDataImage != null) {
                                ThreadUtils.mainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNoDataImage.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                        return result;
                    }
                });
    }

    @NonNull
    @Override
    public BaseAdapter<GamePlayingEntity> getAdapter() {
        return adapter;
    }

    @Override
    public void onRefreshStateChanged(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void onError(Throwable t) {
        ToastUtil.TextToast(t.getMessage());
    }

    @OnClick(R.id.default_tool_bar_imageview)
    void back() {
        finish();
    }

    @Override
    public void onRefresh() {
        refreshTop();
    }

    @Override
    public void onResume() {
        //        refreshTop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*无论在哪个线程发送都在主线程接收
       * 接受任务完成后的通知，刷新数据
       * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LeaveGame result) {
        //刷新数据
        refreshTop();
    }
}
