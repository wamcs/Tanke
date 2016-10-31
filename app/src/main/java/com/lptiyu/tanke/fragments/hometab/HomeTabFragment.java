package com.lptiyu.tanke.fragments.hometab;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetailv2.GameDetailV2Activity;
import com.lptiyu.tanke.activities.gameplaying_v2.GamePlayingV2Activity;
import com.lptiyu.tanke.adapter.HomeTabAdapter;
import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeTabFragment extends MyBaseFragment implements HomeTabContact.IHomeTabView {
    @BindView(R.id.id_stickynavlayout_innerscrollview)
    LRecyclerView recyclerView;//用原生的RecyclerView会导致底部最后一个item显示不全问题，所以这里选择用LRecyclerView
    private HomeTabPresenter presenter;
    private int sortIndex;
    private boolean isLoading;

    public static HomeTabFragment newInstance(int sortIndex) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Conf.SORT_INDEX, sortIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            sortIndex = bundle.getInt(Conf.SORT_INDEX);
            presenter = new HomeTabPresenter(this);
            firstLoadData(sortIndex);
        } else {
            Toast.makeText(getActivity(), "bundle数据传递异常", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void firstLoadData(final int cid) {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.firstLoadGameList(cid);
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                    .OnRetryCallback() {
                @Override
                public void onRetry() {
                    firstLoadData(cid);
                }
            });
        }
    }

    private void setRecyclerViewAdapter(final List<HomeTabEntity> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false));
        HomeTabAdapter adapter = new HomeTabAdapter(getActivity(), list, sortIndex);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeTabEntity homeTabEntity = list.get(position);
                RunApplication.gameId = homeTabEntity.id;
                RunApplication.entity = homeTabEntity;
                Intent intent = new Intent();
                switch (homeTabEntity.play_status) {
                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                        intent.putExtra(Conf.GAME_TYPE, homeTabEntity.type);
                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_TAB);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GameDetailV2Activity.class);
                        break;
                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GamePlayingV2Activity.class);
                        break;
                }
                getActivity().startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                recyclerView.refreshComplete();
            }

            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onBottom() {
                LogUtils.i("到底啦");
                if (isLoading) {
                    return;
                }
                isLoading = true;
                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "加载中", true, false);
                //模拟网络请求
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                        dialog.dismiss();
                    }
                }, 1000);
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {

            }
        });

        //        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
        //                false));
        //        HomeTabAdapter adapter = new HomeTabAdapter(getActivity(), list, sortIndex);
        //        recyclerView.setAdapter(adapter);
        //        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
        //            @Override
        //            public void onClick(int position) {
        //                HomeTabEntity homeTabEntity = list.get(position);
        //                RunApplication.gameId = homeTabEntity.id;
        //                RunApplication.entity = homeTabEntity;
        //                Intent intent = new Intent();
        //                switch (homeTabEntity.play_status) {
        //                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
        //                        intent.putExtra(Conf.GAME_TYPE, homeTabEntity.type);
        //                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_TAB);
        //                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
        //                        intent.setClass(getActivity(), GameDetailV2Activity.class);
        //                        break;
        //                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
        //                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
        //                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
        //                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
        //                        intent.setClass(getActivity(), GamePlayingV2Activity.class);
        //                        break;
        //                }
        //                getActivity().startActivity(intent);
        //            }
        //
        //            @Override
        //            public void onLongClick(int position) {
        //            }
        //        });
        //        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //            @Override
        //            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //                super.onScrolled(recyclerView, dx, dy);
        //            }
        //
        //            @Override
        //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //                super.onScrollStateChanged(recyclerView, newState);
        //                if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >=
        //                        recyclerView.computeVerticalScrollRange() && newState == recyclerView
        //                        .SCROLL_STATE_IDLE) {
        //                    final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "加载中", true, false);
        //                    //模拟网络请求
        //                    new Handler().postDelayed(new Runnable() {
        //                        @Override
        //                        public void run() {
        //                            dialog.dismiss();
        //                        }
        //                    }, 1000);
        //                }
        //            }
        //        });
    }


    @Override
    public void successFirstLoadGameList(List<HomeTabEntity> list) {
        if (list != null && list.size() > 0) {
            setRecyclerViewAdapter(list);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
