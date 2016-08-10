package com.lptiyu.tanke.gamedetails;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-18
 *         email: wonderfulifeel@gmail.com
 */
public class GameDetailsLocationActivity extends BaseActivity {

  private GameDetailsLocationController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_details_location);
    mController = new GameDetailsLocationController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return mController;
  }

}
