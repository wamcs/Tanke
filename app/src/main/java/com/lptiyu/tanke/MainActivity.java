package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.gameplaying.GamePlayingActivity;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionDispatcher.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }

  @Override
  public ActivityController getController() {
    return null;
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    PermissionDispatcher.showCameraWithCheck(this);
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_CAMERA)
  public void openCamera() {
    Intent intent = new Intent();
    intent.setClass(this, CaptureActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.game_playing)
  public void startGamePlayingActivity() {
    Intent intent = new Intent();
    intent.setClass(this, GamePlayingActivity.class);
    startActivity(intent);
  }

}
