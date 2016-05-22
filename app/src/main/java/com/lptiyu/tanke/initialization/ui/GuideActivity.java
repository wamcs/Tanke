package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.controller.GuideController;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class GuideActivity extends BaseActivity {

    ActivityController controller;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_guide);
        controller = new GuideController(this,getWindow().getDecorView());
    }

    @Override
    public ActivityController getController() {
        return controller;
    }
}
