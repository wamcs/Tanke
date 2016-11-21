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
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.adapter.HomeTabAdapter;
import com.lptiyu.tanke.entity.eventbus.EnterGame;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.eventbus.LeaveGame;
import com.lptiyu.tanke.entity.response.HomeTabEntity;
import com.lptiyu.tanke.enums.GameState;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.enums.SortIndex;
import com.lptiyu.tanke.enums.Where;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lptiyu.tanke.RunApplication.entity;

public class HomeTabFragment extends MyBaseFragment implements HomeTabContact.IHomeTabView {
    @BindView(R.id.id_stickynavlayout_innerscrollview)
    LRecyclerView recyclerView;//用原生的RecyclerView会导致底部最后一个item显示不全问题，所以这里选择用LRecyclerView
    private HomeTabPresenter presenter;
    private int cid;
    private boolean isLoading;
    private boolean hasMoreGame = true;
    private LRecyclerViewAdapter lRecyclerViewAdapter;
    private List<HomeTabEntity> totalist;

    public static HomeTabFragment newInstance(int cid) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Conf.CID, cid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            cid = bundle.getInt(Conf.CID);
            presenter = new HomeTabPresenter(this);
            firstLoadGameList();
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

    private void firstLoadGameList() {
        presenter.firstLoadGameList(cid);
    }

    private void reloadGameList() {
        presenter.reloadGameList(cid);
    }

    private void setRecyclerViewAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false));
        HomeTabAdapter adapter = new HomeTabAdapter(getActivity(), totalist, cid);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        recyclerView.setAdapter(lRecyclerViewAdapter);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO 团队赛事待完成
                if (cid == SortIndex.COMPETITION_ACTIVITY) {
                    Toast.makeText(getActivity(), "即将开放，敬请期待", Toast.LENGTH_SHORT).show();
                    return;
                }
                HomeTabEntity homeTabEntity = totalist.get(position);
                if (homeTabEntity.state == GameState.FINISHED) {
                    Toast.makeText(getContext(), "该游戏已下线", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (homeTabEntity.state == GameState.MAINTAINING) {
                    Toast.makeText(getContext(), "该游戏正在维护中", Toast.LENGTH_SHORT).show();
                    return;
                }
                RunApplication.gameId = homeTabEntity.id;
                RunApplication.type = homeTabEntity.type;
                entity = homeTabEntity;
                RunApplication.where = Where.HOME_TAB;
                RunApplication.recordId = -1;
                Intent intent = new Intent();
                switch (homeTabEntity.play_status) {
                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                        intent.putExtra(Conf.GAME_TYPE, homeTabEntity.type);
                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_TAB);
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GameDetailActivity.class);
                        break;
                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                        intent.putExtra(Conf.HOME_TAB_ENTITY, homeTabEntity);
                        intent.setClass(getActivity(), GamePlayingActivity.class);
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
                if (isLoading) {
                    return;
                }
                isLoading = true;
                if (hasMoreGame) {
                    presenter.loadMoreGame(cid);
                }
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {

            }

            @Override
            public void onScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void successFirstLoadGameList(List<HomeTabEntity> list) {
        if (list != null && list.size() > 0) {
            totalist = new ArrayList<>();
            totalist.addAll(list);
            setRecyclerViewAdapter();
        }
    }

    @Override
    public void successReloadGame(List<HomeTabEntity> list) {
        if (list != null && list.size() > 0) {
            totalist.clear();
            totalist.addAll(list);
            lRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void successLoadMoreGame(List<HomeTabEntity> list) {
        if (list != null && list.size() > 0) {
            hasMoreGame = true;
            totalist.addAll(list);
            lRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            hasMoreGame = false;
        }
    }

    @Override
    public void failLoadMoreGame(String errMsg) {
        hasMoreGame = false;
    }

    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GamePointTaskStateChanged result) {
        //刷新数据
        reloadGameList();
    }


    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EnterGame result) {
        //刷新数据
        reloadGameList();
    }

    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LeaveGame result) {
        //刷新数据
        reloadGameList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
