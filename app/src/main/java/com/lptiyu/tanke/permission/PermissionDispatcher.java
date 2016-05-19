package com.lptiyu.tanke.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

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

  public static void showCameraWithCheck(AppCompatActivity activity) {
    nextStepWithCheck(activity, PERMISSION_REQUEST_CODE_CAMERA);
  }

  public static void startLocateWithCheck(AppCompatActivity activity) {
    nextStepWithCheck(activity, PERMISSION_REQUEST_CODE_LOCATION);
  }

  public static void onRequestPermissionsResult(AppCompatActivity activity, int requestCode, int[] grantResults) {

    //permission is granted
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // Permission Granted
      invokeTargetMethodWithRequestCode(activity, requestCode);
    }

    //permission is denied
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE_CAMERA:
        //TODO : intent to setting, open the camera permission
        Timber.e("Camera Permission Denied");
        break;

      case PERMISSION_REQUEST_CODE_LOCATION:
        //TODO : intent to setting, open the camera permission
        Timber.e("Location Permission Denied");
        break;
    }
  }

  private static void invokeTargetMethodWithRequestCode(AppCompatActivity activity, int requestCodeAskPermission) {
    List<Method> targetMethods = PermissionUtil.findTargetMethodWithRequestCode(activity, requestCodeAskPermission);
    for (Method m : targetMethods) {
      Timber.e(m.getName());
      try {
        m.invoke(activity);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  private static void nextStepWithCheck(AppCompatActivity activity, int requestCodeAskPermission) {
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
          if (PermissionUtil.shouldShowCameraRequestRationale(activity)) {
            //TODO : show the rationale why you need this permission
          } else {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCodeAskPermission);
          }
        }
        break;

      case PERMISSION_REQUEST_CODE_LOCATION:
        if (PermissionUtil.hasLocatePermission(activity)) {
          invokeTargetMethodWithRequestCode(activity, requestCodeAskPermission);
          return;
        } else {
          if (PermissionUtil.shouldShowLocateRequestRationale(activity)) {
            //TODO : show the rationale why you need this permission
          } else {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCodeAskPermission);
          }
        }
        break;
    }
  }

}
