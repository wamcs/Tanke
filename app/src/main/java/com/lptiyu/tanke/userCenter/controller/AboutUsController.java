package com.lptiyu.tanke.userCenter.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.initialization.ui.UserProtocolActivity;
import com.lptiyu.tanke.widget.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-29
 *         email: wonderfulifeel@gmail.com
 */
public class AboutUsController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  CustomTextView mToolbarTitle;

  public AboutUsController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);
    init();
  }

  private void init() {
    mToolbarTitle.setText(getString(R.string.about_us_activity));
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

  @OnClick(R.id.activity_about_us_user_protocol)
  void userProtocol() {
    Intent intent = new Intent(getContext(), UserProtocolActivity.class);
    startActivity(intent);
  }
}
