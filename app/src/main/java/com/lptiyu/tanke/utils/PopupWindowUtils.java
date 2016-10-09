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
    private static LayoutInflater inflater;

    private PopupWindowUtils() {
    }

    public static PopupWindowUtils getInstance() {
        if (popupWindowUtils == null) {
            synchronized (PopupWindowUtils.class) {
                if (popupWindowUtils == null) {
                    popupWindowUtils = new PopupWindowUtils();
                }
            }
        }
        return popupWindowUtils;
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
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    public interface OnNetExceptionListener {
        void onClick(View view);
    }

    public void showUsageTip(Context context, View parent, int xOffset, int yOffset) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_usage, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAsDropDown(parent, xOffset, yOffset);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    public void showTaskGuide(Context context, String content, final DismissCallback callback) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_task_guide, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (callback != null) {
                    callback.onDismisss();
                }
            }
        });

        TextView tv_content_tip = (TextView) popupView.findViewById(R.id.tv_content_tip);
        TextView tv_ok = (TextView) popupView.findViewById(R.id.tv_ok);
        tv_content_tip.setText(content);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void showTaskGuide(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_task_guide, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView tv_content_tip = (TextView) popupView.findViewById(R.id.tv_content_tip);
        TextView tv_ok = (TextView) popupView.findViewById(R.id.tv_ok);
        tv_content_tip.setText(content);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 签到成功弹出的PopupWindow
     *
     * @param context
     * @param content
     */
    public void showSucessSignUp(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_home_signup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView tv_show = (TextView) popupView.findViewById(R.id.tv_show);
        tv_show.setText(content);
        popupView.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public interface DismissCallback {
        void onDismisss();
    }
}
