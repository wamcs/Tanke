package com.lptiyu.tanke.gameplaying.task;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.controller.ActivityController;
import com.lptiyu.tanke.base.ui.BaseFragment;
import com.lptiyu.tanke.gameplaying.assist.LocateHelper;
import com.lptiyu.tanke.gameplaying.pojo.LocationPwd;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.global.Conf;
import com.lptiyu.tanke.permission.PermissionDispatcher;
import com.lptiyu.tanke.permission.TargetMethod;
import com.lptiyu.tanke.utils.ToastUtil;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class LocateTaskController extends MultiplyTaskController implements
    BDLocationListener {

  private View answerView;
  private AlertDialog loadingDialog;
  private LocateHelper locateHelper;

  public LocateTaskController(Fragment fragment, ActivityController controller, View view) {
    super(fragment, controller, view);
    locateHelper = new LocateHelper(getContext().getApplicationContext());
    locateHelper.registerLocationListener(this);
    initLoadingDialog();
  }

  @Override
  public void initTaskView() {
    if (answerView == null) {
      answerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_locate_task, null);
      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
      mAnswerArea.addView(answerView, layoutParams);
      mAnswerArea.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          finishTask();
//          mActivityController.openNextTaskIfExist();
          PermissionDispatcher.startLocateWithCheck(((BaseFragment) getFragment()));
        }
      });
      mWebView.loadUrl(mTask.getContent());
    }
  }

  @Override
  public void onReceiveLocation(BDLocation bdLocation) {
    locateHelper.stopLocate();
    mAnswerArea.setClickable(true);
    loadingDialog.dismiss();
    LocationPwd locationPwd = AppData.globalGson().fromJson(mTask.getPwd(), LocationPwd.class);
    if (locationPwd == null) {
      return;
    }
    locationPwd.setRadius(Conf.LOCATE_TASK_POINT_RADIUS);
    if (DistanceUtil.getDistance(locationPwd.getLatLng(), new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude())) < locationPwd.getRadius()) {
      ToastUtil.TextToast(getString(R.string.right_location));
      finishTask();
      mActivityController.openNextTaskIfExist();
    } else {
      ToastUtil.TextToast(getString(R.string.error_location));
    }
  }

  @TargetMethod(requestCode = PermissionDispatcher.PERMISSION_REQUEST_CODE_LOCATION)
  public void getUserCurrentLocation() {
    locateHelper.startLocate();
    mAnswerArea.setClickable(false);
    loadingDialog.show();
  }

  private void initLoadingDialog() {
    if (loadingDialog == null) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null);
      TextView textView = ((TextView) view.findViewById(R.id.loading_dialog_textview));
      textView.setText(getString(R.string.locate_task_locating));
      loadingDialog = new AlertDialog.Builder(getContext())
          .setCancelable(true)
          .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
              mAnswerArea.setClickable(true);
              ToastUtil.TextToast(getString(R.string.cancel_locating));
            }
          })
          .setView(view)
          .create();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionDispatcher.onFragmentRequestPermissionsResult(((BaseFragment) getFragment()), requestCode, permissions, grantResults);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    locateHelper.unRegisterLocationListener(this);
  }
}
