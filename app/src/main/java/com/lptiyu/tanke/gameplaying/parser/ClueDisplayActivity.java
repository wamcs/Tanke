package com.lptiyu.tanke.gameplaying.parser;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;

/**
 * author:wamcs
 * date:2016/7/11
 * email:kaili@hustunique.com
 */
public class ClueDisplayActivity extends BaseActivity {

  ActivityController activityController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_clue_display);
    activityController = new ClueDisplayController(this,getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return activityController;
  }
}
