package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lptiyu.tanke.R;

/**
 * Created by Jason on 2016/11/2.
 */

public class ScanNothingHelper {
    private Context context;
    private PopupWindow popupWindow;
    private int times;
    private Handler handler = new Handler();
    private TextView tvTip;
    private View popupView;
    private DismissCallback callback;

    public ScanNothingHelper(Context context, DismissCallback callback) {
        this.context = context;
        this.callback = callback;
        initPopupWindow();
    }

    private void initTimeTask() {
        times = 3;
        tvTip.setText(String.format(context.getString(R.string.scan_nothing), times));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (times > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvTip.setText(String.format(context.getString(R.string.scan_nothing), --times));
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                });
            }
        }).start();
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_scan_nothing, null);
        tvTip = (TextView) popupView.findViewById(R.id.tv_tip);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (callback != null) {
                    callback.onDismisss();
                }
            }
        });
    }

    public void show() {
        if (popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
        initTimeTask();
    }

    public void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public interface DismissCallback {
        void onDismisss();
    }
}
