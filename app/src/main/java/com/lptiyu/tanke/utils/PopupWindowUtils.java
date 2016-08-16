package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lptiyu.tanke.R;

/**
 * Created by Jason on 2016/7/1.
 */

public class PopupWindowUtils {
    private static PopupWindowUtils popupWindowUtils;

    private PopupWindowUtils() {
    }

    public static PopupWindowUtils getInstance() {
        if (popupWindowUtils == null) {
            return new PopupWindowUtils();
        } else {
            return popupWindowUtils;
        }
    }

    public void showNetExceptionPopupwindow(Context context, final OnNetExceptionListener listener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_net_exception, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_try_again = (TextView) popupView.findViewById(R.id.tv_try_again);

        tv_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    public void showFailLoadPopupwindow(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_fail_load, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        //        TextView tv_fail_load = (TextView) popupView.findViewById(R.id.tv_fail_load);
        //
        //        tv_fail_load.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                popupWindow.dismiss();
        //
        //            }
        //        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    public interface OnNetExceptionListener {
        void onClick(View view);
    }

    //    public void showSharePopupwindow(Context context) {
    //        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //        View popupView = inflater.inflate(R.layout.popup_share, null);
    //        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
    //                .LayoutParams.WRAP_CONTENT, true);
    //        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    //    }
}
