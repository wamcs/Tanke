package com.lptiyu.tanke;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Timber.d("onCreate");
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    PermissionDispatcher.showCameraWithCheck(this);
  }

  @OnClick(R.id.start_trace)
  public void onStartTrace() {
    PermissionDispatcher.startLocateWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // NOTE: delegate the permission handling to generated method
    PermissionDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }


  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_CAMERA)
  public void openCamera() {
    startActivity(new Intent(this, CaptureActivity.class));
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocate() {
    ToastUtil.TextToast("开始定位");
  }

}
