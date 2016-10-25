package com.lptiyu.tanke.permission;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
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
  public static boolean hasPermission(Fragment fragment, String targetPermission) {
    int hasCameraPermission = fragment.getActivity().checkSelfPermission(targetPermission);
    return hasCameraPermission == PackageManager.PERMISSION_GRANTED;
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean hasPermission(AppCompatActivity activity, String targetPermission) {
    int hasCameraPermission = activity.checkSelfPermission(targetPermission);
    return hasCameraPermission == PackageManager.PERMISSION_GRANTED;
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean shouldShowRequestRational(Fragment fragment, String targetPermission) {
    return fragment.getActivity().shouldShowRequestPermissionRationale(targetPermission);
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean shouldShowRequestRational(AppCompatActivity activity, String targetPermission) {
    return activity.shouldShowRequestPermissionRationale(targetPermission);
  }

  public static boolean hasCameraPermission(Fragment fragment) {
    return hasPermission(fragment, Manifest.permission.CAMERA);
  }

  public static boolean hasLocatePermission(Fragment fragment) {
    return hasPermission(fragment, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static boolean hasCameraPermission(AppCompatActivity activity) {
    return hasPermission(activity, Manifest.permission.CAMERA);
  }

  public static boolean hasLocatePermission(AppCompatActivity activity) {
    return hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static boolean shouldShowCameraRequestRationale(Fragment fragment) {
    return shouldShowRequestRational(fragment, Manifest.permission.CAMERA);
  }

  public static boolean shouldShowLocateRequestRationale(Fragment fragment) {
    return shouldShowRequestRational(fragment, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static boolean shouldShowCameraRequestRationale(AppCompatActivity activity) {
    return shouldShowRequestRational(activity, Manifest.permission.CAMERA);
  }

  public static boolean shouldShowLocateRequestRationale(AppCompatActivity activity) {
    return shouldShowRequestRational(activity, Manifest.permission.ACCESS_FINE_LOCATION);
  }

  public static List<Method> findTargetMethodWithRequestCode(Class clazz, int requestCodeAskPermission) {
    List<Method> targetMethods = new ArrayList<>();
    Method[] methods = clazz.getMethods();
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
