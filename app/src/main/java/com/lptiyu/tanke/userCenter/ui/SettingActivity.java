package com.lptiyu.tanke.userCenter.ui;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.userCenter.controller.SettingController;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class SettingActivity extends BaseActivity {
  ActivityController activityController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_setting);
    activityController = new SettingController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return activityController;
  }
}
