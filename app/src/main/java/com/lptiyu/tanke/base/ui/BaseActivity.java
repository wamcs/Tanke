package com.lptiyu.tanke.base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.controller.ControllerHolder;
import com.lptiyu.tanke.mybase.MyBaseActivity;


/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/10/15
 *
 * @author ldx
 */
public abstract class BaseActivity extends MyBaseActivity implements ControllerHolder {

    ActivityController controller;

    @Override
    protected void onStart() {
        super.onStart();
        controller = getController();
        if (controller != null) {
            controller.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null) {
            controller.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (controller != null) {
            controller.onStop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != controller) {
            controller.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (controller != null) {
            controller.onDestroy();
        }
        //        RunApplication.getInstance().finishActivity(this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != controller) {
            controller.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (null != controller && !controller.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (null != controller) {
            controller.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public abstract ActivityController getController();

}
