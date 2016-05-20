package com.lptiyu.tanke.gamedisplay;

import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/19
 *
 * @author ldx
 */
public class GameDisplayController extends ActivityController {

  //As a view controller
  GameDisplayActivity activity;

  public GameDisplayController(GameDisplayActivity activity, View view) {
    super(activity, view);
    this.activity = activity;
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {

  }

  @OnClick(R.id.tool_bar)
  public void clickToolbar() {

  }


}
