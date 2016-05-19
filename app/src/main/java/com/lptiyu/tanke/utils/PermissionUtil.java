package com.lptiyu.tanke.utils;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-18
 *         email: wonderfulifeel@gmail.com
 */
public class PermissionUtil {

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean hasPermission(Context context, String targetPermission) {
    int hasCameraPermission = context.checkSelfPermission(targetPermission);
    return hasCameraPermission == PackageManager.PERMISSION_GRANTED;
  }

  public static boolean hasCameraPermission(Context context) {
    return hasPermission(context, Manifest.permission.CAMERA);
  }

  public static void showCameraWithCheck(Context context) {
    if (hasCameraPermission(context)) {

    } else {

    }
  }

}
