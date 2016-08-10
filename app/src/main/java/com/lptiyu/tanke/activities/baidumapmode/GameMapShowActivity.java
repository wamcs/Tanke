package com.lptiyu.tanke.activities.baidumapmode;


import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class GameMapShowActivity extends BaseActivity {

  ActivityController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_playing);
    mController = new GameMapShowController(this,getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }
}
