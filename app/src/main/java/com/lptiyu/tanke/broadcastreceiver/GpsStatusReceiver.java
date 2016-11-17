package com.lptiyu.tanke.broadcastreceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by Jason on 2016/11/16.
 */

public class GpsStatusReceiver extends BroadcastReceiver {
    private boolean currentGPSState = false;
    private static GpsStatusReceiver receiver;
    private static AlertDialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            if (context instanceof Activity) {
                initGPS((Activity) context);
            }
        }
    }

    public static void initGPS(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            // 转到手机设置界面，用户设置GPS,设置完成后返回到原来的界面
            dialog = new AlertDialog.Builder(activity).setMessage("系统检测到您关闭了GPS，为了定位更加精确，请重新打开GPS")
                    .setCancelable(false)
                    .setPositiveButton("确定", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    // 转到手机设置界面，用户设置GPS
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    activity.startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                                }
                            }).show();
        }
    }

    /**
     * GPS是否可用
     *
     * @param context
     * @return
     */
    private boolean isGPSEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean on = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return on;
    }

    /**
     * 注册监听广播
     *
     * @param context
     * @throws Exception
     */
    public static void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        receiver = new GpsStatusReceiver();
        context.registerReceiver(receiver, filter);
    }

    /**
     * 解除注册
     *
     * @param context
     * @throws Exception
     */
    public static void unregister(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }


    //    /**
    //     * 改变GPS状态
    //     *
    //     * @param context
    //     * @throws Exception
    //     */
    //    public static void changeGPSState(Context context) throws Exception {
    //        boolean before = isGPSEnable(context);
    //        ContentResolver resolver = context.getContentResolver();
    //        if (before) {
    //            Settings.Secure.putInt(resolver, Settings.Secure.LOCATION_MODE, Settings.Secure
    // .LOCATION_MODE_OFF);
    //        } else {
    //            Settings.Secure.putInt(resolver, Settings.Secure.LOCATION_MODE, Settings.Secure
    //                    .LOCATION_MODE_HIGH_ACCURACY);
    //        }
    //        currentGPSState = isGPSEnable(context);
    //    }
}