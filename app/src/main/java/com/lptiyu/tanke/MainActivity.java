package com.lptiyu.tanke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.lptiyu.tanke.activities.directionrun.DirectionRunActivity;
import com.lptiyu.tanke.entity.response.SignUpResponse;
import com.lptiyu.tanke.fragments.home.HomeFragment;
import com.lptiyu.tanke.fragments.messagesystem.MessageFragment;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.update.UpdateHelper;
import com.lptiyu.tanke.userCenter.UserCenterFragment;
import com.lptiyu.tanke.utils.DBManager;
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

public class MainActivity extends MyBaseActivity implements UpdateHelper.IUpdateCallback {

    public int mCurrentIndex = 2;
    @BindView(R.id.page_1)
    ImageView tab1;
    @BindView(R.id.page_2)
    ImageView tab2;
    @BindView(R.id.page_3)
    ImageView tab3;

    private UpdateHelper updateHelper;
    ArrayList<Fragment> fragments = new ArrayList<>(3);
    Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initTab();
        signUpAndStartLocation();
        updateHelper = new UpdateHelper(this);
        updateHelper.checkForUpdate();
        updateHelper.setUpdateCallback(this);
    }

    @Override
    public void isUpdate(boolean isUpdate) {
        if (!isUpdate) {
            recoveryDRData();
        }
    }

    private void recoveryDRData() {
        if (Accounts.isHaveDRData()) {
            new AlertDialog.Builder(this).setMessage("检测到你有未完成的跑步记录，是否恢复？").setNegativeButton("放弃", new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Accounts.setHaveDRData(false);
                            DBManager.getInstance(MainActivity.this).deleteDRLocalAll();
                            DBManager.getInstance(MainActivity.this).deleteLocationAll();
                        }
                    }).setPositiveButton("恢复", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    initGPS();
                }
            }).setCancelable(false).show();
        }
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

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("为了定位更加精确，请打开GPS");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent(MainActivity.this, DirectionRunActivity.class);
            intent.putExtra(Conf.RECOVERY, true);
            startActivity(intent);
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
        if (fragments != null) {
            fragments.clear();
        }
        fragments.add(new HomeFragment());
        fragments.add(new MessageFragment());
        fragments.add(new UserCenterFragment());
        changeTab(0);
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


