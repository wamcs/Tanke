package com.lptiyu.tanke.gameplaying.task;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class BaseTaskActivity extends BaseActivity {

  private ActivityController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_base_task);

    mController = new BaseTaskController(this, getWindow().getDecorView());

  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
