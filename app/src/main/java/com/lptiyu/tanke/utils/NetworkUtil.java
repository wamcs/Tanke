package com.lptiyu.tanke.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.lptiyu.tanke.global.AppData;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class NetworkUtil {

  //TODO permission
  public static boolean checkNetworkConnected() {
    ConnectivityManager connMgr = (ConnectivityManager) AppData.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    return connMgr.getActiveNetworkInfo() != null;
  }
}
