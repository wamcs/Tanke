package com.lptiyu.tanke.initialization.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.lptiyu.tanke.base.controller.ActivityController;

/**
 * author:wamcs
 * date:2016/5/22
 * email:kaili@hustunique.com
 */

//Logo show activity

public class SplashActivityController extends ActivityController {


    public SplashActivityController(AppCompatActivity activity, View view) {
        super(activity, view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
