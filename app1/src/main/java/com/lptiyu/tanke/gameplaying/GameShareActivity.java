package com.lptiyu.tanke.gameplaying;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class GameShareActivity extends BaseActivity {

  private GameShareController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_share);
    mController = new GameShareController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
