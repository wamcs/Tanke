package com.lptiyu.tanke.initialization.ui;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.initialization.controller.UserProtocolController;

/**
 * author:wamcs
 * date:2016/5/19
 * email:kaili@hustunique.com
 */
public class UserProtocolActivity extends BaseActivity {

  ActivityController controller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_protocol);
    controller = new UserProtocolController(this, getWindow().getDecorView());

  }

  @Override
  public ActivityController getController() {
    return controller;
  }
}
