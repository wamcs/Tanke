package com.lptiyu.tanke.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lptiyu.tanke.global.AppData;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class NetworkUtil {

  //TODO permission
  public static boolean checkIsNetworkConnected() {
    ConnectivityManager connMgr = (ConnectivityManager) AppData.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    return connMgr.getActiveNetworkInfo() != null;
  }

  public static boolean isWlanAvailable() {
    ConnectivityManager manager = (ConnectivityManager) AppData.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    return (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING);
  }


}
