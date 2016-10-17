package com.lptiyu.tanke.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.lptiyu.tanke.mybase.MyBaseFragment;

import java.util.List;

/**
 * Created by Jason on 2016/10/10.
 */

public class PointTaskFragmentPagerAdapter extends BaseFragmentPagerAdapter<MyBaseFragment> {
    public PointTaskFragmentPagerAdapter(FragmentManager fm, List<? extends MyBaseFragment> fragments) {
        super(fm, fragments);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
