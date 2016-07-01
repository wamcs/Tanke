package com.lptiyu.tanke.userCenter.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.global.Accounts;
import com.lptiyu.tanke.initialization.ui.LoginActivity;
import com.lptiyu.tanke.userCenter.ui.AboutUsActivity;
import com.lptiyu.tanke.utils.ShaPreferManager;
import com.lptiyu.tanke.widget.CustomTextView;
import com.lptiyu.tanke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author:wamcs
 * date:2016/6/9
 * email:kaili@hustunique.com
 */
public class SettingController extends ActivityController {

  @BindView(R.id.default_tool_bar_textview)
  CustomTextView mToolbarText;

//  @BindView(R.id.setting_activity_msg_push)
//  SwitchButton mMsgPush;
  @BindView(R.id.setting_activity_mobile_vibrate)
  SwitchButton mVibrate;
//  @BindView(R.id.setting_activity_screen_light)
//  SwitchButton mScreenLight;

  @BindView(R.id.setting_activity_logout)
  CustomTextView mLogout;

  public SettingController(AppCompatActivity activity, View view) {
    super(activity, view);
    ButterKnife.bind(this, view);

    mToolbarText.setText(getString(R.string.setting));
    init();
  }

  private void init() {
//    mMsgPush.setChecked(ShaPreferManager.getMsgPush());
    mVibrate.setChecked(ShaPreferManager.getMobileVibrate());
//    mScreenLight.setChecked(ShaPreferManager.getScreenLight());
  }

//  @OnClick(R.id.setting_activity_feedback)
//  void onFeedback() {
//    FeedbackAgent agent = new FeedbackAgent(getContext());
//    agent.startDefaultThreadActivity();
//  }

  @OnClick(R.id.setting_activity_logout)
  void onLogoutClicked() {
    Accounts.logOut();
    Intent intent = new Intent(getActivity(), LoginActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.default_tool_bar_imageview)
  void back() {
    finish();
  }

//  @OnClick(R.id.setting_activity_msg_push)
//  void msgPush() {
//    if (mMsgPush.isChecked()) {
//      ShaPreferManager.setMsgPush(true);
//    } else {
//      ShaPreferManager.setMsgPush(false);
//    }
//  }

  @OnClick(R.id.setting_activity_mobile_vibrate)
  void mobileVirate() {
    if (mVibrate.isChecked()) {
      ShaPreferManager.setMobileVibrate(true);
    } else {
      ShaPreferManager.setMobileVibrate(false);
    }
  }

//  @OnClick(R.id.setting_activity_screen_light)
//  void screenLight() {
//    if (mScreenLight.isChecked()) {
//      ShaPreferManager.setScreenLight(true);
//    } else {
//      ShaPreferManager.setScreenLight(false);
//    }
//  }

  @OnClick(R.id.setting_activity_about_us)
  void startAboutUs() {
    startActivity(new Intent(getActivity(), AboutUsActivity.class));
  }

}
