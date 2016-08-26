package com.lptiyu.tanke.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Jason on 2016/8/16.
 */
public class ProgressDialogHelper {
    private static ProgressDialog initDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIndeterminate(false);//true不显示进度，false显示进度
        dialog.setMessage("加载中...");
        return dialog;
    }

    public static ProgressDialog getHorizontalProgressDialog(Context context) {
        ProgressDialog dialog = initDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条的形式为水平进度条
        return dialog;
    }

    public static ProgressDialog getSpinnerProgressDialog(Context context) {
        ProgressDialog dialog = initDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        return dialog;
    }
}
