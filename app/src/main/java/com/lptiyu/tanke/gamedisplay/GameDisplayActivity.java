package com.lptiyu.tanke.gamedisplay;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.io.net.HttpService;
import com.lptiyu.tanke.utils.NetworkUtil;
import com.lptiyu.tanke.utils.ToastUtil;


/**
 * Used to display the game list.
 */
public class GameDisplayActivity extends BaseActivity {

  ActivityController controller;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game_display);
    controller = new GameDisplayController(this, getWindow().getDecorView());
  }

  @Override
  public ActivityController getController() {
    return controller;
  }

  private void init() {
    if (!NetworkUtil.checkNetworkConnected()) {
      ToastUtil.TextToast(R.string.no_network);
      return;
    }

    HttpService.getInstance().getGameService()
        .getGamePage();

  }

  public void loading() {

  }


}
