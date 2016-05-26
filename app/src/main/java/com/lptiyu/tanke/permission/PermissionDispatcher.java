package com.lptiyu.tanke.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.base.ui.BaseActivity;

import java.lang.reflect.Method;
import java.util.List;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-19
 *         email: wonderfulifeel@gmail.com
 */
public class PermissionDispatcher {

  public static final int PERMISSION_REQUEST_CODE_CAMERA = 1;
  public static final int PERMISSION_REQUEST_CODE_LOCATION = 2;

  public static void showCameraWithCheck(BaseActivity activity) {
    nextStepWithCheck(activity, PERMISSION_REQUEST_CODE_CAMERA);
  }

  public static void startLocateWithCheck(BaseActivity activity) {
    nextStepWithCheck(activity, PERMISSION_REQUEST_CODE_LOCATION);
  }

  public static void onRequestPermissionsResult(final BaseActivity activity, int requestCode, @NonNull String[] permissions, int[] grantResults) {

    //permission is denied
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE_CAMERA:
        if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          invokeTargetMethodWithRequestCode(activity, requestCode);
          return;
        }
        showMessageOKCancel(activity, activity.getString(R.string.camera_permission_rationale));
        break;

      case PERMISSION_REQUEST_CODE_LOCATION:
        if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          invokeTargetMethodWithRequestCode(activity, requestCode);
          return;
        }
        //TODO : intent to setting, open the camera permission
        showMessageOKCancel(activity, activity.getString(R.string.camera_permission_rationale));
        break;
    }
  }

  private static void invokeTargetMethodWithRequestCode(BaseActivity activity, int requestCodeAskPermission) {
    List<Method> targetActivityMethods = PermissionUtil.findTargetMethodWithRequestCode(activity.getClass(), requestCodeAskPermission);
    List<Method> targetControllerMethods = PermissionUtil.findTargetMethodWithRequestCode(activity.getController().getClass(), requestCodeAskPermission);
    for (Method m : targetActivityMethods) {
      Timber.e(m.getName());
      try {
        m.invoke(activity);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    for (Method m : targetControllerMethods) {
      Timber.e(m.getName());
      try {
        m.invoke(activity.getController());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  private static void nextStepWithCheck(BaseActivity activity, int requestCodeAskPermission) {
    if (Build.VERSION.SDK_INT < 23) {
      invokeTargetMethodWithRequestCode(activity, requestCodeAskPermission);
      return;
    }
    switch (requestCodeAskPermission) {
      case PERMISSION_REQUEST_CODE_CAMERA:
        if (PermissionUtil.hasCameraPermission(activity)) {
          invokeTargetMethodWithRequestCode(activity, requestCodeAskPermission);
          return;
        } else {
          if (PermissionUtil.shouldShowLocateRequestRationale(activity)) {
            //TODO : show the rationale why you need this permission
            showMessageOKCancel(activity, activity.getString(R.string.camera_permission_rationale));
            return;
          }
          activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCodeAskPermission);
        }
        break;

      case PERMISSION_REQUEST_CODE_LOCATION:
        //TODO : 这里好迷阿，定位权限和相机权限默认状态根本就不同，定位权限处于一种半拒绝的状态，相机权限处于一种询问的状态
        if (PermissionUtil.hasLocatePermission(activity)) {
          invokeTargetMethodWithRequestCode(activity, requestCodeAskPermission);
          return;
        } else {
          if (!PermissionUtil.shouldShowLocateRequestRationale(activity)) {
            //TODO : show the rationale why you need this permission
            showMessageOKCancel(activity, activity.getString(R.string.location_permission_rationale));
            return;
          }
          activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCodeAskPermission);
        }
        break;
    }
  }

  private static void showMessageOKCancel(BaseActivity activity, String message) {
    final BaseActivity appCompatActivity = activity;
    new AlertDialog.Builder(activity)
        .setMessage(message)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            startPermissionSettingActivity(appCompatActivity);
          }
        })
        .setNegativeButton("Cancel", null)
        .create()
        .show();
  }

  private static void startPermissionSettingActivity(BaseActivity appCompatActivity) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    intent.setData(Uri.fromParts("package", appCompatActivity.getPackageName(), null));
    appCompatActivity.startActivity(intent);
  }

}
