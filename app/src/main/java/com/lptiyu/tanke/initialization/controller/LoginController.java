package com.lptiyu.tanke.initialization.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.ButterKnife;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class LoginController extends ActivityController {


    public LoginController(AppCompatActivity activity, View view) {
        super(activity, view);
        ButterKnife.bind(this, view);
    }

    private void init(){

    }
}
