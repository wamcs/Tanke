package com.lptiyu.tanke.activities.initialization.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lptiyu.tanke.MainActivity;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.mybase.MyBaseActivity;

import java.io.File;

/**
 * author:wamcs
 * date:2016/5/22
 * email:kaili@hustunique.com
 */
public class SplashActivity extends MyBaseActivity {
    private File imageFile;
    private ImageView splashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashView = new ImageView(this);
        setContentView(splashView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        process();
    }

    private void init() {
        imageFile = createStorageFile();
        if (imageFile == null) {
            throw new IllegalStateException("SplashActivity : splash file create failed.");
        }
        splashView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private File createStorageFile() {
        return imageFile = new File(getCacheDir(), "splash.jpg");
    }

    public void process() {
        if (isFirstInApp()) {
            startActivity(new Intent(this, GuideActivity.class));
            finish();
        } else {
            Glide.with(this).load(imageFile).error(R.drawable.bg_splash).into(splashView);
            smoothStartNext();
        }
    }

    protected void smoothStartNext() {
        final Intent intent = new Intent();
        if (isAccountsValid()) {
            intent.setClass(this, MainActivity.class);
        } else {
            intent.setClass(this, LoginActivity.class);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
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
