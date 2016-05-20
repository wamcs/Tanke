package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.controller.CompleteInformationController;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class CompleteInformationActivity extends BaseActivity {

    ActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_infomation);
        controller = new CompleteInformationController(this,getWindow().getDecorView());
    }

    @Override
    public ActivityController getController() {
        return controller;
    }
}
