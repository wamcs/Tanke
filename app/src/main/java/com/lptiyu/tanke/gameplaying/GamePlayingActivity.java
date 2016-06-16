package com.lptiyu.tanke.gameplaying;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.global.Conf;

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
