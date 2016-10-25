package com.lptiyu.tanke.messagesystem;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class SystemWebActivity extends BaseActivity {

  private SystemWebController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_system_web);
    mController = new SystemWebController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }
}
