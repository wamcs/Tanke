package com.lptiyu.tanke.gameplaying.task;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.lptiyu.tanke.utils.DirUtils;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class DistinguishTaskController extends MultiplyTaskController {

  private File mTempFile;
  private View answerView;
  private static final int CAMERA_REQUEST_CODE = 1;
  private static final String DISTINGUISH_TASK_TEMP_PHOTO = "distinguish_task_temp_photo.png";

  public DistinguishTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_distinguish_task, null);
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
  public void startTakePhoto() {
    if (null == mTempFile) {
      mTempFile = new File(DirUtils.getTempDirectory(), DISTINGUISH_TASK_TEMP_PHOTO);
    }
    if (!mTempFile.exists()) {
      try {
        if (mTempFile.createNewFile()) {
          if (mTempFile.exists() && mTempFile.isFile()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
          } else {
            Timber.e("create temp distinguish photo file error");
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case CAMERA_REQUEST_CODE:
        //TODO : to check whether the temp photo file(mTempFile) is match with pwd photo
        break;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionDispatcher.onFragmentRequestPermissionsResult(((BaseFragment) getFragment()), requestCode, permissions, grantResults);
  }

}
