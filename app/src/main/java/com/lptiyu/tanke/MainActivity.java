package com.lptiyu.tanke;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.lptiyu.tanke.utils.PermissionUtil;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    Timber.d("onCreate");
  }

  @OnClick(R.id.scanner)
  public void onClick() {
    PermissionUtil.showCameraWithCheck(this);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case REQUEST_CODE_ASK_PERMISSIONS:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission Granted
          startActivity(new Intent(this, CaptureActivity.class));
        } else {
          // Permission Denied
          ToastUtil.TextToast("Camera Denied");
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @TargetApi(23)
  public void requestCameraPermission() {
    int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
    if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.CAMERA},
          REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      startActivity(new Intent(this, CaptureActivity.class));
    }
  }

}
