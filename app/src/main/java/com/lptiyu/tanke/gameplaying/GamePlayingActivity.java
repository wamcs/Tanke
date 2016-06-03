package com.lptiyu.tanke.gameplaying;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.global.Conf;

import timber.log.Timber;

public class GamePlayingActivity extends BaseActivity {

  private GamePlayingController mController;

  //TODO : delete this params, use Long.MIN_VALUE instead
  static final long TEMP_GAME_ID = 1000000001L;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_playing);

    long gameId = getIntent().getLongExtra(Conf.GAME_ID, TEMP_GAME_ID);
    if (RecordsUtils.isGameStartedFromDisk(gameId)) {
      mController = new HistoryGamePlayingController(this, getWindow().getDecorView());
    } else {
      mController = new NewGamePlayingController(this, getWindow().getDecorView());
    }
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
