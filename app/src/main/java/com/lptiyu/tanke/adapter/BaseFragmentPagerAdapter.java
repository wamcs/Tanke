package com.lptiyu.tanke.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author 顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * @Title: ListViewFragment
 * @Package com.gxz.stickynavlayout.fragments
 * @Description:
 * @date 15/12/29
 * @time 上午11:50
 */
public abstract class BaseFragmentPagerAdapter<T> extends FragmentPagerAdapter {

    public List<? extends T> fragments;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<? extends T> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
