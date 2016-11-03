package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.lptiyu.tanke.entity.response.SignUpResponse;
import com.lptiyu.tanke.fragments.home.HomeFragment;
import com.lptiyu.tanke.fragments.messagesystem.MessageFragment;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.update.UpdateHelper;
import com.lptiyu.tanke.userCenter.UserCenterFragment;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.PopupWindowUtils;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.tanke.utils.xutils3.RequestParamsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsHelper;
import com.lptiyu.tanke.utils.xutils3.XUtilsRequestCallBack;
import com.lptiyu.tanke.utils.xutils3.XUtilsUrls;

import org.xutils.http.RequestParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MyBaseActivity {

    public int mCurrentIndex = 2;
    @BindView(R.id.page_1)
    ImageView tab1;
    @BindView(R.id.page_2)
    ImageView tab2;
    @BindView(R.id.page_3)
    ImageView tab3;

    private LocationHelper locationHelper;
    ArrayList<Fragment> fragments = new ArrayList<>(3);
    private UpdateHelper updateHelper;
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //定位当前城市和经纬度
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Accounts.setCity(aMapLocation.getCity());
                Accounts.setCityCode(aMapLocation.getCityCode());
                Accounts.setLatitude((float) aMapLocation.getLatitude());
                Accounts.setLongitude((float) aMapLocation.getLongitude());
                LogUtils.i("定位信息：" + aMapLocation.getCity() + aMapLocation.getCityCode() + ", 经纬度：" + aMapLocation
                        .getLatitude() + ", " + aMapLocation.getLongitude());

            }
        });
        locationHelper.setOnceLocation(true);
        initTab();
        signUpAndStartLocation();
    }

    private void signUp() {
        //自动签到
        int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        //如果当前的天数跟存储的天数不相等，则表示用户上一次签到是在昨天
        if (dayIndex != Accounts.getDayIndex()) {
            signIn(Accounts.getId());
        } else {
            if (!Accounts.isSignUp()) {
                signIn(Accounts.getId());
            }
        }
    }

    private void signUpAndStartLocation() {
        if (NetworkUtil.checkIsNetworkConnected()) {
            locationHelper.startLocation();
            signUp();
        } else {
            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    PopupWindowUtils.getInstance().showNetExceptionPopupwindow(MainActivity.this, new PopupWindowUtils
                            .OnRetryCallback() {
                        @Override
                        public void onRetry() {
                            signUpAndStartLocation();
                        }
                    });
                }
            });
        }
    }

    private void signIn(long id) {
        RequestParams params = RequestParamsHelper.getBaseRequestParam(XUtilsUrls.HOME_SIGN_IN);
        params.addBodyParameter("uid", id + "");
        XUtilsHelper.getInstance().get(params, new XUtilsRequestCallBack<SignUpResponse>() {
            @Override
            protected void onSuccess(SignUpResponse signUpResponse) {
                if (signUpResponse.status == 1) {
                    PopupWindowUtils.getInstance().showSucessSignUp(MainActivity.this, signUpResponse.data.tip + "");
                    Accounts.setSignUp(true);
                    Accounts.setDayIndex(Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
                }
            }

            @Override
            protected void onFailed(String errorMsg) {
                ToastUtil.TextToast(errorMsg);
            }
        }, SignUpResponse.class);
    }

    @IntDef({0, 1, 2})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @interface page {
    }


    public void selectTab(@page int index) {
        if (mCurrentIndex == index) {
            return;
        }
        selectTab(index == 0, tab1, R.drawable.tab_home_selected, R.drawable.tab_home_normal);
        selectTab(index == 1, tab2, R.drawable.tab_message_selected, R.drawable.tab_message_normal);
        selectTab(index == 2, tab3, R.drawable.tab_mine_selected, R.drawable.tab_mine_normal);
        mCurrentIndex = index;
    }

    private void selectTab(boolean select, ImageView view, int resSelected, int resUnselected) {
        if (select) {
            view.setImageResource(resSelected);
        } else {
            view.setImageResource(resUnselected);
        }
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.TextToast(getString(R.string.exit));
            exitTime = System.currentTimeMillis();
        } else {
            RunApplication.getInstance().AppExit();
        }
    }

    private void initTab() {
        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new UserCenterFragment());
        changeTab(0);
        updateHelper = new UpdateHelper(this);
        updateHelper.checkForUpdate();
        selectTab(0);
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
        if (index == mCurrentIndex) {
            return;
        }
        selectTab(index);
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
}


