package com.lptiyu.tanke;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

  FragmentManager mFragmentManager;

  MainActivityController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mController = new MainActivityController(this, getWindow().getDecorView());
    ButterKnife.bind(this);
    init();
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

  private void init() {
    mFragmentManager = getSupportFragmentManager();
  }

}
