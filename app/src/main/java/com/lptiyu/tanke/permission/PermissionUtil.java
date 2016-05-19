package com.lptiyu.tanke.permission;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : xiaoxiaoda
 * date: 16-5-18
 * email: wonderfulifeel@gmail.com
 */

/**
 * util to check permission is exist or not
 */
class PermissionUtil {

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean hasPermission(AppCompatActivity activity, String targetPermission) {
    int hasCameraPermission = activity.checkSelfPermission(targetPermission);
    return hasCameraPermission == PackageManager.PERMISSION_GRANTED;
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean shouldShowRequestRational(AppCompatActivity activity, String targetPermission) {
    return activity.shouldShowRequestPermissionRationale(targetPermission);
  }


  public static boolean hasCameraPermission(AppCompatActivity activity) {
    return hasPermission(activity, Manifest.permission.CAMERA);
  }

  public static boolean hasLocatePermission(AppCompatActivity activity) {
    return hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static boolean shouldShowCameraRequestRationale(AppCompatActivity activity) {
    return shouldShowRequestRational(activity, Manifest.permission.CAMERA);
  }

  public static boolean shouldShowLocateRequestRationale(AppCompatActivity activity) {
    return shouldShowRequestRational(activity, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static List<Method> findTargetMethodWithRequestCode(AppCompatActivity activity, int requestCodeAskPermission) {
    List<Method> targetMethods = new ArrayList<>();
    Class<?> clazz = activity.getClass();
    Method[] methods = clazz.getDeclaredMethods();
    for (Method m : methods) {
      Annotation annotation = m.getAnnotation(TargetMethod.class);
      if (annotation == null) {
        continue;
      }
      if (requestCodeAskPermission == ((TargetMethod) annotation).requestCode()) {
        targetMethods.add(m);
      }
    }
    return targetMethods;
  }

}
