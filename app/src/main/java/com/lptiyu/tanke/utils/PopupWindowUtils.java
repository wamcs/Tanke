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
            synchronized (PopupWindowUtils.class) {
                if (popupWindowUtils == null) {
                    popupWindowUtils = new PopupWindowUtils();
                }
            }
        }
        return popupWindowUtils;
    }

    public void showNetExceptionPopupwindow(Context context, final OnRetryCallback listener) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_net_exception, inflater);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_try_again = (TextView) popupView.findViewById(R.id.tv_try_again);

        tv_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener != null) {
                    listener.onRetry();
                }
            }
        });
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    public void showDRSignUpPopupWindow(Context context, String exp, String score, int redWallet) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_sign_up_direction_run, inflater);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvExp = (TextView) popupView.findViewById(R.id.tv_exp_value);
        TextView tvScore = (TextView) popupView.findViewById(R.id.tv_score_value);
        TextView tvRedWallet = (TextView) popupView.findViewById(R.id.tv_red_wallet);
        tvExp.setText("+" + exp);
        tvScore.setText("+" + score);
        if (redWallet <= 0) {
            tvRedWallet.setVisibility(View.GONE);
        } else {
            tvRedWallet.setVisibility(View.VISIBLE);
            tvRedWallet.setText("获得现金红包" + redWallet / 100.0f + "元");
        }
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        popupWindow.setAnimationStyle(R.style.Popup_Animation_fade_in);
    }

    public void showFailLoadPopupwindow(Context context) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_fail_load, inflater);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, -100);
    }

    public void showUsageTip(Context context, View parent, int xOffset, int yOffset) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_usage, inflater);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAsDropDown(parent, xOffset, yOffset);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    public void showTaskGuide(Context context, String content, final DismissCallback callback) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_task_guide, inflater);
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
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_task_guide, inflater);
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
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_home_signup, inflater);
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

    public void showLeaveGamePopup(Context context, final OnClickPopupListener listener) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_abandon_game, inflater);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.Popup_Animation_fly_from_bottom_enter);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
        TextView tvTip = (TextView) popupView.findViewById(R.id.tv_tip);
        tvTip.setText("放弃游戏将会失去游戏进度，已获得的经验值不会丢失");

        popupView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener != null) {
                    listener.sure();
                }
            }
        });
    }

    private LayoutInflater getInflater(Context context) {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public PopupWindow getPopupWindow(Context context, int layoutRes, int width, int height, int gravity) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(layoutRes, inflater);
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popupView, gravity, 0, 0);
        return popupWindow;
    }

    public PopupWindow getPopupWindow(View popupView, int width, int height, int gravity) {
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popupView, gravity, 0, 0);
        return popupWindow;
    }


    private View getPopupView(int layoutRes, LayoutInflater inflater) {
        return inflater.inflate(layoutRes, null);
    }

    public void showPlayAgainPopup(Context context, final OnClickPopupListener listener) {
        LayoutInflater inflater = getInflater(context);
        View popupView = getPopupView(R.layout.popup_abandon_game, inflater);
        final PopupWindow popupWindow = getPopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        TextView tvTip = (TextView) popupView.findViewById(R.id.tv_tip);
        tvTip.setText("重新玩不会删除已完成的游戏记录，是否继续?");

        popupView.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (listener != null) {
                    listener.sure();
                }
            }
        });
    }

    public interface DismissCallback {
        void onDismisss();
    }

    public interface OnClickPopupListener {
        void sure();
    }

    public interface OnRetryCallback {
        void onRetry();
    }
}
