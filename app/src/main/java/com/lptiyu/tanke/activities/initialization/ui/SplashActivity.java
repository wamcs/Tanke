package com.lptiyu.tanke.activities.initialization.ui;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.mybase.MyBaseActivity;
import com.lptiyu.tanke.utils.LocationHelper;
import com.lptiyu.tanke.utils.LogUtils;
import com.lptiyu.tanke.utils.MobileDisplayHelper;

import java.io.File;

/**
 * author:wamcs
 * date:2016/5/22
 * email:kaili@hustunique.com
 */
public class SplashActivity extends MyBaseActivity {
    private File imageFile;
    private ImageView splashView;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashView = new ImageView(this);
        setContentView(splashView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        startLocation();
        getMobileInfo();
    }

    private void getMobileInfo() {
        Point point = MobileDisplayHelper.getMobileWidthHeight(this);
        LogUtils.i("当前手机的宽：" + point.x);
        LogUtils.i("当前手机的高：" + point.y);
        LogUtils.i("DisplayMetrics:" + MobileDisplayHelper.getMobileDisplayMetrics(this));
        LogUtils.i("desity:" + MobileDisplayHelper.getMobileDesity(this));
        LogUtils.i("desityDpi:" + MobileDisplayHelper.getMobileDesityDpi(this));
        LogUtils.i("scaleDesity:" + MobileDisplayHelper.getMobileScaleDesity(this));
    }

    private void startLocation() {
        //定位当前城市和经纬度
        locationHelper = new LocationHelper(this, new LocationHelper.OnLocationResultListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //断网情况下会返回空字符串
                if (TextUtils.isEmpty(aMapLocation.getCityCode())) {
                    Accounts.setCityCode("027");
                } else {
                    Accounts.setCityCode(aMapLocation.getCityCode());
                }
                if (TextUtils.isEmpty(aMapLocation.getCity())) {
                    Accounts.setCity("武汉");
                } else {
                    Accounts.setCity(aMapLocation.getCity());
                }
                Accounts.setLatitude((float) aMapLocation.getLatitude());
                Accounts.setLongitude((float) aMapLocation.getLongitude());
                smoothStartNext();
                LogUtils.i(aMapLocation.getCityCode() + "," + aMapLocation.getCity() + ", (" + aMapLocation.getLatitude
                        () + "," + aMapLocation.getLongitude() + ")");

            }
        });
        locationHelper.setOnceLocation(true);
        locationHelper.startLocation();
    }

    private void init() {
        imageFile = new File(getCacheDir(), "splash.jpg");
        if (imageFile == null) {
            throw new IllegalStateException("SplashActivity : splash file create failed.");
        }
        splashView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(imageFile).error(R.drawable.bg_splash).into(splashView);
    }

    protected void smoothStartNext() {
        final Intent intent = new Intent();
        if (isFirstInApp()) {
            intent.setClass(this, GuideActivity.class);
        } else {
            if (isAccountsValid()) {
                intent.setClass(this, MainActivity.class);
            } else {
                intent.setClass(this, LoginActivity.class);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected boolean isFirstInApp() {
        return AppData.isFirstInApp();
    }

    protected boolean isAccountsValid() {
        return Accounts.isLogin();
    }
}
