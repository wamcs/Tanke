package com.lptiyu.tanke.fragments.hometab;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.gameplaying_v2.GamePlayingV2Activity;
import com.lptiyu.tanke.adapter.HomeDisplayAdapter;
import com.lptiyu.tanke.entity.eventbus.NotifyHomeRefreshData;
import com.lptiyu.tanke.entity.response.HomeGameList;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeTabFragment extends MyBaseFragment implements HomeTabContact.IHomeTabView {

    @BindView(R.id.id_stickynavlayout_innerscrollview)
    LRecyclerView mRecycleView;
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
        EventBus.getDefault().register(this);

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void setRecyclerViewAdapter(final List<HomeGameList> list) {
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        HomeDisplayAdapter mDataAdapter = new HomeDisplayAdapter(getActivity(), list, sortIndex);
        LRecyclerViewAdapter adapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecycleView.setAdapter(adapter);
        mRecycleView.setPullRefreshEnabled(true);//必须设置为true,否则头部置顶后无法再拉下来
        //        mRecycleView.setArrowImageView(R.drawable.key);
        mRecycleView.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                mRecycleView.refreshComplete();
            }

            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onBottom() {
                RecyclerViewStateUtils.setFooterViewState(getActivity(), mRecycleView, 10,
                        LoadingFooter.State.Loading, null);
                Toast.makeText(getActivity(), "到底啦", Toast.LENGTH_SHORT).show();
                RecyclerViewStateUtils.setFooterViewState(mRecycleView,
                        LoadingFooter.State.Normal);
                mRecycleView.refreshComplete();
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {

            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeGameList homeGameList = list.get(position);
                RunApplication.gameId = homeGameList.id;
                Intent intent = new Intent();
                switch (homeGameList.play_status) {
                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                        intent.putExtra(Conf.GAME_ID, homeGameList.id);
                        intent.putExtra(Conf.GAME_TYPE, homeGameList.type);
                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_TAB);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeGameList);
                        intent.setClass(getActivity(), GameDetailActivity.class);
                        break;
                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                        intent.putExtra(Conf.GAME_ID, homeGameList.id);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeGameList);
                        intent.setClass(getActivity(), GamePlayingV2Activity.class);
                        break;
                }
                getActivity().startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                ToastUtil.TextToast("长按：" + list.get(position).title + "");
            }
        });
    }


    @Override
    public void successFirstLoadGameList(List<HomeGameList> list) {
        if (list != null && list.size() > 0) {
            setRecyclerViewAdapter(list);
        }
    }

    /*无论在哪个线程发送都在主线程接收，接收到通知后刷新数据源*/
    @Subscribe
    public void onEventMainThread(NotifyHomeRefreshData nhfd) {
        firstLoadData(sortIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
