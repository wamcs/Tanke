package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.entity.response.SignUpResponse;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
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
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    MainActivityController mController;

    /**
     * Init value is a non-zero value, and then will be set to value.
     */
    public int mCurrentIndex = 2;

    @BindView(R.id.page_1)
    ImageView tab1;

    @BindView(R.id.page_2)
    ImageView tab2;

    @BindView(R.id.page_3)
    ImageView tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mController = new MainActivityController(this, getWindow().getDecorView());

        //定位当前城市和经纬度
        LocationHelper locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
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
        locationHelper.startLocation();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                init();
                signUp();
            }
        });
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

    @Override
    public ActivityController getController() {
        return mController;
    }

    private void init() {
        selectTab(0);
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
}


