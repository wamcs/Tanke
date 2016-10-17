package com.lptiyu.tanke.gamedisplay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.xutils3.SwipeRefreshLayoutUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class GameDisplayFragment extends MyBaseFragment {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    @BindView(R.id.relative_layout)
    RelativeLayout toolBar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    private GameDisplayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_display, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        SwipeRefreshLayoutUtils.setSwipeStyle(swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);

                // loadNetWorkData();刷新只是个假动作，并不真正刷新，减少服务器的请求压力
                //判断下网络是否正常吧
                if (!NetworkUtil.checkIsNetworkConnected()) {
                    swipe.setRefreshing(false);
                    showNetUnConnectDialog();
                }

                if (adapter.getItemCount() < 3)//为什么是小于3，getItemCount没有任何数据时候也会返回2
                {
                }

                //TODO 当网络请求完毕时才隐藏刷新标志
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swipe.setRefreshing(false);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        recyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(getContext()));

        adapter = new GameDisplayAdapter(this);

        recyclerView.setAdapter(adapter);
        //用于下拉刷新监听
        ElasticTouchListener listener = new ElasticTouchListener();
        recyclerView.addOnItemTouchListener(listener);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                }
            }
        });

        listener.setOnRefreshListener(new ElasticTouchListener.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
        } else {
            swipe.setRefreshing(false);
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils.OnRetryCallback() {
            @Override
            public void onRetry() {
                if (!NetworkUtil.checkIsNetworkConnected()) {
                    swipe.setRefreshing(false);
                    showNetUnConnectDialog();
                } else if (adapter.getItemCount() < 3)//如果有的话就不更新
                {
                }
            }
        });
    }

    public GameDisplayAdapter getAdapter() {
        return adapter;
    }

    public void loading(boolean enable) {
        if (!enable) {
            if (adapter.isFooterVisible()) {
                adapter.hideFooter();
            }
        }
    }
}
