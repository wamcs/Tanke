package com.lptiyu.tanke.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lptiyu.tanke.R;

/**
 * Created by Jason on 2016/10/28.
 */

public class GameOverHelper implements View.OnClickListener {

    private View popupView;
    private PopupWindow popupWindow;
    private Context context;
    private TextView tv_gameName;
    private TextView tv_gameIntroduction;
    private ProgressBar progressBar_score;
    private ProgressBar progressBar_exp;
    private TextView tv_redWallet;
    private OnPopupWindowDismissCallback callback;
    private Handler handler = new Handler();
    private int MAX_PROGRESS = 1000;
    private TextView tv_add_exp;
    private TextView tv_add_score;

    public GameOverHelper(Context context, OnPopupWindowDismissCallback callback) {
        this.context = context;
        this.callback = callback;
        initPopupwindow();
    }

    public void bindData(String gameName, String gameIntroduction, String score, String exp, String redWallet) {
        tv_gameName.setText(gameName + "");
        tv_gameIntroduction.setText(gameIntroduction + "");
        tv_add_score.setText(score + "");
        tv_add_exp.setText(exp + "");
        tv_redWallet.setText(redWallet + "");
    }

    private void initPopupwindow() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_game_over, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView) popupView.findViewById(R.id.img_close);
        tv_gameName = (TextView) popupView.findViewById(R.id.tv_game_name);
        tv_gameIntroduction = (TextView) popupView.findViewById(R.id.tv_game_introduction);
        progressBar_score = (ProgressBar) popupView.findViewById(R.id.progressBar_score_value);
        progressBar_exp = (ProgressBar) popupView.findViewById(R.id.progressBar_exp_value);
        tv_add_score = (TextView) popupView.findViewById(R.id.tv_add_score);
        tv_add_exp = (TextView) popupView.findViewById(R.id.tv_add_exp);
        tv_redWallet = (TextView) popupView.findViewById(R.id.tv_red_wallet_value);
        ImageView img_wechatShare = (ImageView) popupView.findViewById(R.id.img_wechat_share);
        ImageView img_qq = (ImageView) popupView.findViewById(R.id.img_qq_share);
        ImageView img_wechatMoment = (ImageView) popupView.findViewById(R.id.img_wechat_moment_share);
        img_close.setOnClickListener(this);
        img_wechatShare.setOnClickListener(this);
        img_qq.setOnClickListener(this);
        img_wechatMoment.setOnClickListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (callback != null) {
                    callback.onDismiss();
                }
            }
        });
    }

    private int progress = 0;

    public void showPopup() {
        if (popupView != null && popupWindow != null) {
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
        progressBar_exp.setMax(MAX_PROGRESS);
        progressBar_score.setMax(MAX_PROGRESS);
        progress = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progress <= MAX_PROGRESS) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar_exp.setProgress(progress);
                            progressBar_score.setProgress(progress);
                            progress++;
                        }
                    });
                }
            }
        }).start();
    }

    public void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        String imgUrl = "http://www.lptiyu.com/images/phone.png";
        String shareUrl = "http://www.lptiyu.com/images/phone.png";
        switch (v.getId()) {
            case R.id.img_close:
                hidePopup();
                break;
            case R.id.img_wechat_share:
                ShareHelper.share(ShareHelper.SHARE_WECHAT_FRIENDS, "测试", "测试", imgUrl, shareUrl);
                break;
            case R.id.img_qq_share:
                ShareHelper.share(ShareHelper.SHARE_QQ, "测试", "测试", imgUrl, shareUrl);
                break;
            case R.id.img_wechat_moment_share:
                ShareHelper.share(ShareHelper.SHARE_WECHAT_CIRCLE, "测试", "测试", imgUrl, shareUrl);
                break;
        }
    }

    public interface OnPopupWindowDismissCallback {
        void onDismiss();
    }
}
