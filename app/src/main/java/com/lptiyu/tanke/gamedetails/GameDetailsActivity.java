package com.lptiyu.tanke.gamedetails;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

public class GameDetailsActivity extends BaseActivity {

  GameDetailsController controller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_details);
    controller = new GameDetailsController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return controller;
  }
}
