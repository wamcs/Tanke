package com.lptiyu.tanke.gameplaying;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class GamePlayingActivity extends BaseActivity {

  private GamePlayingController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_playing);
    mController = new GamePlayingController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
