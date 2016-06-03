package com.lptiyu.tanke.gameplaying.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.ToastUtil;
import com.lptiyu.zxinglib.android.CaptureActivity;

import static android.app.Activity.RESULT_OK;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class ScanTaskController extends MultiplyTaskController {

  private View answerView;
  private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

  public ScanTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_scan_task, null);
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      mAnswerArea.addView(answerView, layoutParams);
      mAnswerArea.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finishTask();
          mActivityController.openNextTaskIfExist();
//          PermissionDispatcher.showCameraWithCheck(((BaseFragment) getFragment()));
        }
      });
      mWebView.loadUrl(mTask.getContent());

    }
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_CAMERA)
  public void startScanQrCodeActivity() {
    Intent intent = new Intent(getContext(), CaptureActivity.class);
    startActivityForResult(intent, CAMERA_PERMISSION_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Bundle b = data.getExtras();
      String str = b.getString("data");
      if (str == null|| str.length() == 0) {
        ToastUtil.TextToast(getString(R.string.scan_error));
        return;
      }
      //TODO : upload str to server to check pwd
    } else {
      ToastUtil.TextToast(getString(R.string.scan_error));
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionDispatcher.onFragmentRequestPermissionsResult(((BaseFragment) getFragment()), requestCode, permissions, grantResults);
  }
}
