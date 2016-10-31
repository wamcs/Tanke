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
import com.lptiyu.tanke.entity.response.UpLoadGameRecordResult;

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
    private final String SUCESS = "恭喜你，找到答案了！";
    private final String CLOSE = "关闭";
    private final String CONTINUE_GAME = "继续游戏";
    private AnimationDrawable anim;
    public TextView popup_tv_exp;
    public TextView popup_tv_score;
    public TextView popup_tv_red_wallet;
    public RelativeLayout popup_rl_red_wallet;
    private RelativeLayout popup_rl_exp;
    private RelativeLayout popup_rl_score;

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
    public void showSuccessResult(UpLoadGameRecordResult result) {
        isOK = true;
        stopAnim();
        if (popup_img_result != null)
            popup_img_result.setImageResource(R.drawable.task_result_right);
        if (popup_tv_btn != null)
            popup_tv_btn.setText(CONTINUE_GAME);
        if (popup_tv_result != null)
            popup_tv_result.setText(SUCESS);
        if (popup_tv_exp != null && result != null) {
            popup_tv_exp.setText("+" + result.get_exp);
        }
        if (popup_tv_score != null && result != null) {
            popup_tv_score.setText("+" + result.get_extra_points);
        }
        if (popup_tv_red_wallet != null && result != null) {
            popup_tv_red_wallet.setText("获得" + result.get_extra_money + "元红包");
        }
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
        if (popup_rl_exp != null) {
            popup_rl_exp.setVisibility(View.GONE);
        }
        if (popup_rl_score != null) {
            popup_rl_score.setVisibility(View.GONE);
        }
        if (popup_rl_red_wallet != null) {
            popup_rl_red_wallet.setVisibility(View.GONE);
        }
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
        if (popup_rl_exp != null) {
            popup_rl_exp.setVisibility(View.GONE);
        }
        if (popup_rl_score != null) {
            popup_rl_score.setVisibility(View.GONE);
        }
        if (popup_rl_red_wallet != null) {
            popup_rl_red_wallet.setVisibility(View.GONE);
        }
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

        popup_tv_btn = (TextView) popupView.findViewById(R.id.tv_continue_scan);
        popup_img_result = (ImageView) popupView.findViewById(R.id.img_result);
        popup_tv_result = (TextView) popupView.findViewById(R.id.tv_result_tip);
        popup_tv_exp = (TextView) popupView.findViewById(R.id.tv_exp_value);
        popup_tv_score = (TextView) popupView.findViewById(R.id.tv_score_value);
        popup_tv_red_wallet = (TextView) popupView.findViewById(R.id.tv_red_wallet_value);
        popup_rl_red_wallet = (RelativeLayout) popupView.findViewById(R.id.rl_red_wallet);
        popup_rl_exp = (RelativeLayout) popupView.findViewById(R.id.rl_exp);
        popup_rl_score = (RelativeLayout) popupView.findViewById(R.id.rl_score);
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
        if (rl_submitting != null) {
            rl_submitting.setVisibility(View.GONE);
        }
    }

    public interface TaskResultCallback {
        void onSuccess();
    }
}
