package com.lptiyu.tanke.activities.pointtasknew;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.adapter.PointTaskFragmentPagerAdapter;
import com.lptiyu.tanke.entity.Point;
import com.lptiyu.tanke.entity.eventbus.NotifyPointTaskV2RefreshData;
import com.lptiyu.tanke.enums.PointTaskStatus;
import com.lptiyu.tanke.fragments.pointtask.EmptyFragment;
import com.lptiyu.tanke.fragments.pointtask.PointTaskFragment;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.mybase.MyBaseFragment;
import com.lptiyu.tanke.widget.DepthPageTransformer;
import com.lptiyu.tanke.widget.GalleryViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PointTaskV2Activity extends MyBaseActivity {

    @BindView(R.id.view_pager)
    GalleryViewPager mViewPager;
    private final double MAX_PARCEL = 0.9d;
    private ArrayList<Point> point_list;
    private ArrayList<MyBaseFragment> totallist;
    private int max_index = -1;
    private PointTaskFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_task_v2);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initData();
        initView();
    }

    private void initData() {
        point_list = RunApplication.gameRecord.game_detail.point_list;
        if (point_list != null) {
            totallist = new ArrayList<>();
            for (int i = 0; i < point_list.size(); i++) {
                totallist.add(PointTaskFragment.create(i));
                if (point_list.get(i).status == PointTaskStatus.UNSTARTED) {
                    max_index = i - 1;
                    break;
                }
            }
            if (max_index == -1) {//全部都已完成
                max_index = point_list.size() - 1;
            } else {
                //将最后一个fragment替换为EmptyFragment
                totallist.remove(totallist.size() - 1);
                totallist.add(EmptyFragment.create());
            }
        }
    }

    private void initView() {
        adapter = new PointTaskFragmentPagerAdapter
                (getSupportFragmentManager(), totallist);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageMargin(30);// 设置页面间距
        mViewPager.setOffscreenPageLimit(2);//缓存页数
        mViewPager.setCurrentItem(RunApplication.currentPointIndex);// 设置起始位置
        mViewPager.setPageTransformer(true, new DepthPageTransformer());//设置切换动画
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * @param position 当前item的index
             * @param positionOffset 偏移百分比
             * @param positionOffsetPixels 偏移像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == max_index && positionOffset >= MAX_PARCEL) {
                    mViewPager.setScanScroll(false);
                    mViewPager.setCurrentItem(position);
                    mViewPager.setScanScroll(true);
                } else {
                    mViewPager.setScanScroll(true);
                }
            }

            @Override
            public void onPageSelected(int position) {
                RunApplication.currentPointIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /*无论在哪个线程发送都在主线程接收*/
    @Subscribe
    public void onEventMainThread(NotifyPointTaskV2RefreshData result) {
        if (totallist.size() == point_list.size()) {
            return;
        }
        totallist.remove(totallist.size() - 1);
        totallist.add(PointTaskFragment.create(++max_index));
        totallist.add(EmptyFragment.create());
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
