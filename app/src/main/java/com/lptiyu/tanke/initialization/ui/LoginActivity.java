package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.RunApplication;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.controller.LoginController;

import timber.log.Timber;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class LoginActivity extends BaseActivity {

    ActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        controller = new LoginController(this, getWindow().getDecorView());
    }

    @Override
    public ActivityController getController() {
        return controller;
    }

    @Override
    public void onBackPressed() {
      RunApplication.getInstance().AppExit();
    }
}
