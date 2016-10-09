package com.lptiyu.tanke.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.lptiyu.tanke.mybase.MyBaseFragment;

import java.util.List;

/**
 * @author 顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * @Title: ListViewFragment
 * @Package com.gxz.stickynavlayout.fragments
 * @Description:
 * @date 15/12/29
 * @time 上午11:50
 */
public class HomeTabFragmentPagerAdapter extends BaseFragmentPagerAdapter<MyBaseFragment> {

    private List<String> titles;

    public HomeTabFragmentPagerAdapter(FragmentManager fm, List<? extends MyBaseFragment> fragments,
                                       List<String> titles) {
        super(fm, fragments);
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles == null && titles.size() == fragments.size() ? "步道探秘" : titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
