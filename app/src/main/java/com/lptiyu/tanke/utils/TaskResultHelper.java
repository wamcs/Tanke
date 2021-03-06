package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lptiyu.tanke.R;

/**
 * Created by Jason on 2016/8/20.
 */
public class TaskResultHelper {
    private Context context;
    public TextView popup_tv_btn;
    public ImageView popup_img_result;
    public TextView popup_tv_result;
    public PopupWindow popupWindow;
    public View popupView;
    public boolean isOK;
    private TaskResultCallback callback;
    private ImageView imgAnim;
    public RelativeLayout rl_submitting;

    private final String FAIL = "什么都没有发现";
    private final String NET_EXCEPTION = "网络错误";
    private final String SUCESS = "找到新线索";
    private final String CLOSE = "关闭";
    private final String LOOK = "查看";
    private AnimationDrawable anim;

    public TaskResultHelper(Context context, ImageView imageView, TaskResultCallback callback) {
        this.imgAnim = imageView;
        this.callback = callback;
        this.context = context;
        initPopupwindow();
        initAnim();
    }

    public TaskResultHelper(Context context, RelativeLayout rl_submitting, ImageView imageView, TaskResultCallback
            callback) {
        this.imgAnim = imageView;
        this.rl_submitting = rl_submitting;
        this.callback = callback;
        this.context = context;
        initPopupwindow();
        initAnim();
    }

    /**
     * 展示成功信息
     */
    public void showSuccessResult() {
        isOK = true;
        stopAnim();
        if (popup_img_result != null)
            popup_img_result.setImageResource(R.drawable.task_result_right);
        if (popup_tv_btn != null)
            popup_tv_btn.setText(LOOK);
        if (popup_tv_result != null)
            popup_tv_result.setText(SUCESS);
        showPopup();
    }

    /**
     * 展示失败信息
     */
    public void showFailResult() {
        isOK = false;
        stopAnim();
        if (popup_img_result != null)
            popup_img_result.setImageResource(R.drawable.task_result_wrong);
        if (popup_tv_btn != null)
            popup_tv_btn.setText(CLOSE);
        if (popup_tv_result != null)
            popup_tv_result.setText(FAIL);
        showPopup();
    }

    /**
     * 展示网络异常信息
     */
    public void showNetException() {
        isOK = false;
        stopAnim();
        if (popup_tv_btn != null)
            popup_tv_btn.setText(CLOSE);
        if (popup_img_result != null)
            popup_img_result.setImageResource(R.drawable.task_result_wrong);
        if (popup_tv_result != null)
            popup_tv_result.setText(NET_EXCEPTION);
        showPopup();
    }

    public void showPopup() {
        if (popupView != null && popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
    }

    public void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void initPopupwindow() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_scan_result, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        popupWindow.setAnimationStyle(R.style.Popup_Animation);

        popup_tv_btn = (TextView) popupView.findViewById(R.id.tv_continue_scan);
        popup_img_result = (ImageView) popupView.findViewById(R.id.img_result);
        popup_tv_result = (TextView) popupView.findViewById(R.id.tv_result_tip);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isOK) {
                    //成功之后的回调
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } else {
                    hidePopup();
                }
            }
        });

        popup_tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        if (imgAnim != null) {
            imgAnim.setBackgroundResource(R.drawable.anim_upload_record);
            anim = (AnimationDrawable) imgAnim.getBackground();
        }
    }

    /**
     * 开启动画
     */
    public void startAnim() {
        if (anim != null) {
            anim.start();
        }
        //        if (imgAnim != null)
        //            imgAnim.setVisibility(View.VISIBLE);
        if (rl_submitting != null) {
            rl_submitting.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        if (anim != null) {
            anim.stop();
        }
        //        if (imgAnim != null)
        //            imgAnim.setVisibility(View.GONE);
        if (rl_submitting != null) {
            rl_submitting.setVisibility(View.GONE);
        }
    }

    public interface TaskResultCallback {
        void onSuccess();
    }
}
