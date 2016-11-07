package com.lptiyu.tanke.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gxz.PagerSlidingTabStrip;
import com.gxz.library.StickyNavLayout;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.activities.gamedetail.GameDetailActivity;
import com.lptiyu.tanke.activities.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.adapter.BannerPagerAdapter;
import com.lptiyu.tanke.adapter.HomeHotRecyclerViewAdapter;
import com.lptiyu.tanke.adapter.HomeTabFragmentPagerAdapter;
import com.lptiyu.tanke.entity.eventbus.EnterGame;
import com.lptiyu.tanke.entity.eventbus.GamePointTaskStateChanged;
import com.lptiyu.tanke.entity.eventbus.LeaveGame;
import com.lptiyu.tanke.entity.response.Banner;
import com.lptiyu.tanke.entity.response.HomeBannerAndHot;
import com.lptiyu.tanke.entity.response.HomeSort;
import com.lptiyu.tanke.entity.response.HomeSortList;
import com.lptiyu.tanke.entity.response.Recommend;
import com.lptiyu.tanke.enums.PlayStatus;
import com.lptiyu.tanke.fragments.hometab.HomeTabFragment;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.interfaces.OnRecyclerViewItemClickListener;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeFragment extends MyBaseFragment implements HomeContact.IHomeView {
    @BindView(R.id.id_stickynavlayout_indicator)
    PagerSlidingTabStrip pagerSlidingTabStrip;
    @BindView(R.id.id_stickynavlayout_viewpager)
    ViewPager viewPager;
    //    @BindView(R.id.store_house_ptr_frame)
    //    PtrClassicFrameLayout mPtrFrame;
    @BindView(R.id.id_stick)
    StickyNavLayout stickyNavLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.viewPager_banner)
    ViewPager viewPagerBanner;
    @BindView(R.id.recyclerView_hot)
    RecyclerView recyclerViewHot;
    HomePresenter presenter;
    private List<MyBaseFragment> fragments;
    private ArrayList<String> titles;
    private HomeTabFragmentPagerAdapter adapter;
    private List<Banner> bannerList;
    private ArrayList<Recommend> recommendList;
    private BannerPagerAdapter bannerAdapter;
    private HomeHotRecyclerViewAdapter hotAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        presenter = new HomePresenter(this);
        firstLoadData();
    }

    private void firstLoadData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            presenter.firstLoadBannerAndHot();
            presenter.loadSort();
        } else {
            getActivity().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getActivity(), new PopupWindowUtils
                            .OnRetryCallback() {
                        @Override
                        public void onRetry() {
                            firstLoadData();
                        }
                    });
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvTitle.setText(Accounts.getNickName());
    }

    //根据返回结果设置tab
    private void setTab(List<HomeSort> category) {
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        for (HomeSort homeSort : category) {
            fragments.add(HomeTabFragment.newInstance(homeSort.cid));
            titles.add(homeSort.name);
        }

        adapter = new HomeTabFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(titles.size() - 1);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setTextSize(13);
        pagerSlidingTabStrip.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void successFirstLoadBannerAndHot(HomeBannerAndHot homeBannerAndHot) {
        if (homeBannerAndHot != null) {
            if (homeBannerAndHot.banner_list != null) {
                if (bannerList == null)
                    bannerList = new ArrayList<>();
                bannerList.addAll(homeBannerAndHot.banner_list);
                setVPAdapter();
            }
            if (homeBannerAndHot.recommend_list != null) {
                if (recommendList == null)
                    recommendList = new ArrayList<>();
                recommendList.addAll(homeBannerAndHot.recommend_list);
                setHotAdapter();
            }
        } else {
            Toast.makeText(getActivity(), "暂无banner和热门推荐数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void successLoadSort(HomeSortList homeSortList) {
        if (homeSortList != null) {
            List<HomeSort> category = homeSortList.category;
            if (category != null && category.size() > 0) {
                setTab(category);
            }
        } else {
            Toast.makeText(getActivity(), "暂无分类列表", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void successReloadBannerAndHot(HomeBannerAndHot homeBannerAndHot) {
        if (homeBannerAndHot != null) {
            if (homeBannerAndHot.banner_list != null) {
                bannerList.clear();
                bannerList.addAll(homeBannerAndHot.banner_list);
                bannerAdapter.notifyDataSetChanged();
            }
            if (homeBannerAndHot.recommend_list != null) {
                recommendList.clear();
                recommendList.addAll(homeBannerAndHot.recommend_list);
                hotAdapter.notifyDataSetChanged();
            }
        }
    }

    private void refreshData() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            if (presenter != null) {
                presenter.reloadBannerAndHot();
            }
        } else {
            PopupWindowUtils.getInstance().showNetExceptionPopupwindow(getContext(), new PopupWindowUtils
                    .OnRetryCallback() {
                @Override
                public void onRetry() {
                    refreshData();
                }
            });
        }
    }

    private void setHotAdapter() {
        if (recommendList == null) {
            return;
        }
        if (recommendList.size() <= 3) {
            recyclerViewHot.setLayoutManager(new GridLayoutManager(getActivity(), recommendList.size()));
        } else {
            recyclerViewHot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,
                    false));
        }
        hotAdapter = new HomeHotRecyclerViewAdapter(getActivity(), recommendList);
        recyclerViewHot.setAdapter(hotAdapter);
        hotAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(int position) {
                Recommend recommend = recommendList.get(position);
                RunApplication.gameId = Long.parseLong(recommend.id);
                RunApplication.entity = recommend;
                RunApplication.type = recommend.type;
                Intent intent = new Intent();
                switch (recommend.play_status) {
                    case PlayStatus.NO_STATUS:
                    case PlayStatus.NEVER_ENTER_GANME://从未玩过游戏，进入到游戏详情界面
                        intent.setClass(getActivity(), GameDetailActivity.class);
                        intent.putExtra(Conf.FROM_WHERE, Conf.HOME_HOT);
                        break;
                    case PlayStatus.GAME_OVER://游戏结束，暂不考虑
                    case PlayStatus.HAVE_ENTERED_BUT_NOT_START_GAME://进入过但没开始游戏，进入到游戏详情界面
                    case PlayStatus.HAVE_STARTED_GAME://进入并且已经开始游戏，进入到玩游戏界面
                        intent.setClass(getActivity(), GamePlayingActivity.class);
                        break;
                }
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    private void setVPAdapter() {
        bannerAdapter = new BannerPagerAdapter(getContext(), bannerList);
        viewPagerBanner.setAdapter(bannerAdapter);
        viewPagerBanner.setCurrentItem(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.img_home_menu, R.id.scanner})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_home_menu:
                break;
            case R.id.scanner:
                break;
        }
    }

    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GamePointTaskStateChanged result) {
        //刷新数据
        refreshData();
    }

    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EnterGame result) {
        //刷新数据
        refreshData();
    }

    /*无论在哪个线程发送都在主线程接收
   * 接受任务完成后的通知，刷新数据
   * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LeaveGame result) {
        //刷新数据
        refreshData();
    }
}
