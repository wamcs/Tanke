package com.lptiyu.tanke.location;

import android.os.Bundle;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseActivity;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;


/**
 * The activity , user choose the location
 */
public class LocateUserActivity extends BaseActivity {

  private LocateUserActivityController mController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_locate);

    mController = new LocateUserActivityController(this, getWindow().getDecorView());
    PermissionDispatcher.startLocateWithCheck(this);
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void startLocateUserCity() {
    if (mController != null) {
      mController.startLocate();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }


  @Override
  public ActivityController getController() {
    return mController;
  }
}
