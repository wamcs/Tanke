package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.controller.SplashActivityController;

/**
 * author:wamcs
 * date:2016/5/22
 * email:kaili@hustunique.com
 */
public class SplashActivity extends BaseActivity{

    ActivityController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        controller = new SplashActivityController(this,getWindow().getDecorView());

    }

    @Override
    public ActivityController getController(){
        return controller;
    }


}
