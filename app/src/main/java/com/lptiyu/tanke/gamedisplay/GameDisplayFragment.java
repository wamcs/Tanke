package com.lptiyu.tanke.gamedisplay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.MainActivityController;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.pojo.City;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.xutils3.SwipeRefreshLayoutUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class GameDisplayFragment extends BaseFragment {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    private GameDisplayController controller;

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
        controller = new GameDisplayController(this, (MainActivityController) getActivityController(), view);
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
                    controller.refreshTop();
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
        RunApplication.setDisplayAdapter(adapter);

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
                    if (controller != null && !controller.isRefreshing()) {
                        controller.refreshBottom();
                    }
                }
            }
        });

        listener.setOnRefreshListener(new ElasticTouchListener.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!controller.isRefreshing()) {
                    controller.refreshTop();
                }
            }
        });
    }

    private void loadNetWorkData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            controller.refreshTop();
        } else {
            swipe.setRefreshing(false);
            showNetUnConnectDialog();
        }
    }

    // 网络异常对话框
    private void showNetUnConnectDialog() {
        PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                .OnNetExceptionListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtil.checkIsNetworkConnected()) {
                    swipe.setRefreshing(false);
                    showNetUnConnectDialog();
                }
                else if (adapter.getItemCount() < 3)
                {
                    controller.refreshTop();
                }
            }
        });
    }

    public GameDisplayAdapter getAdapter() {
        return adapter;
    }

    public void changeToCurrentCityDialog(final City city) {
        new AlertDialog.Builder(getContext())
                .setMessage(String.format(getString(R.string.change_city_dialog_message), city.getName()))
                .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controller.changeCurrentCity(city);
                    }
                })
                .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.v("用户取消了City(%s)的修改", city.getName());
                    }
                })
        ;
    }

    public void loading(boolean enable) {
        if (!enable) {
            if (adapter.isFooterVisible()) {
                adapter.hideFooter();
            }
        }
    }

    public void loadingError(Throwable t) {
        Timber.d(t, "loading Error...");
    }

    @Override
    public GameDisplayController getController() {
        return controller;
    }

}
