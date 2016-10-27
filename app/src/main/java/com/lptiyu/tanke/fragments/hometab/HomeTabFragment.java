package com.lptiyu.tanke.fragments.hometab;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetailv2.GameDetailV2Activity;
import com.lptiyu.tanke.activities.gameplaying_v2.GamePlayingV2Activity;
import com.lptiyu.tanke.adapter.HomeDisplayAdapter;
import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeTabFragment extends MyBaseFragment implements HomeTabContact.IHomeTabView {

    @BindView(R.id.id_stickynavlayout_innerscrollview)
    RecyclerView recyclerView;
    private HomeTabPresenter presenter;
    private int sortIndex;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        HomeDisplayAdapter adapter = new HomeDisplayAdapter(getActivity(), list, sortIndex);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                HomeTabEntity homeTabEntity = list.get(position);
                RunApplication.gameId = homeTabEntity.id;
                RunApplication.entity = homeTabEntity;
                Intent intent = new Intent();
                switch (homeTabEntity.play_status) {
                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                        //                        intent.putExtra(Conf.GAME_ID, homeTabEntity.id);
                        intent.putExtra(Conf.GAME_TYPE, homeTabEntity.type);
                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_TAB);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GameDetailV2Activity.class);
                        break;
                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                        //                        intent.putExtra(Conf.GAME_ID, homeTabEntity.id);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GamePlayingV2Activity.class);
                        break;
                }
                getActivity().startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
            }
        });
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
        // .SCROLL_STATE_IDLE) {
        //                    Toast.makeText(getActivity(), "到底啦", Toast.LENGTH_SHORT).show();
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
