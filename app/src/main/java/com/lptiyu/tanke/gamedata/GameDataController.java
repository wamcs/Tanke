package com.lptiyu.tanke.gamedata;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-2
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataController extends ActivityController{

  @BindView(R.id.default_tool_bar_textview)
  TextView mToolbarTitle;

  public GameDataController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.game_data));
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    back();
  }

}
