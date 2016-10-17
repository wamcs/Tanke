package com.lptiyu.tanke;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.fragments.home.HomeFragment;
import com.lptiyu.tanke.fragments.messagesystem.MessageListFragment;
import com.lptiyu.tanke.update.UpdateHelper;
import com.lptiyu.tanke.userCenter.UserCenterFragment;
import com.lptiyu.tanke.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/21
 *
 * @author ldx
 */
public class MainActivityController extends ActivityController {

    ArrayList<Fragment> fragments = new ArrayList<>(3);

    MainActivity activity;
    private UpdateHelper updateHelper;

    private long exitTime = 0;
    int mCurrentIndex = -1;

    Fragment mCurrentFragment;

    public MainActivityController(MainActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
        this.activity = activity;
        init();
    }


    private void init() {

        //        fragments.add(new GameDisplayFragment());
        fragments.add(new HomeFragment());
        fragments.add(new MessageListFragment());
        fragments.add(new UserCenterFragment());
        changeTab(0);
        updateHelper = new UpdateHelper(getActivity());
        updateHelper.checkForUpdate();
    }

    @OnClick(R.id.page_1)
    public void page_1() {
        changeTab(0);
    }

    @OnClick(R.id.page_2)
    public void page_2() {
        changeTab(1);
    }

    @OnClick(R.id.page_3)
    public void page_3() {
        changeTab(2);
    }

    private void changeTab(@MainActivity.page int index) {
        //noinspection WrongConstant
        if (index == mCurrentIndex) {
            return;
        }
        activity.selectTab(index);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFragment) {
            ft.hide(mCurrentFragment);
        }
        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                fragments.get(index).getClass().getName());

        if (null == fragment) {
            //如fragment为空，则之前未添加此Fragment。便从集合中取出
            fragment = fragments.get(index);
        }
        mCurrentFragment = fragment;
        mCurrentIndex = index;

        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!fragment.isAdded()) {
            ft.add(R.id.fragment_container, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.TextToast(getString(R.string.exit));
            exitTime = System.currentTimeMillis();
        } else {
            RunApplication.getInstance().AppExit();
        }
        return true;
    }

}
