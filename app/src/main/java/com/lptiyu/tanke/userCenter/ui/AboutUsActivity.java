package com.lptiyu.tanke.userCenter.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.userCenter.controller.AboutUsController;

public class AboutUsActivity extends BaseActivity {

  private AboutUsController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about_us);
    mController = new AboutUsController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }
}
