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

    //TODO : get some info from intent to change controller
    mController = new NewGamePlayingController(this, getWindow().getDecorView());

  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
