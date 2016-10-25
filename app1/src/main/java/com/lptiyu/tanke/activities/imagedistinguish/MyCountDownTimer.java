package com.lptiyu.tanke.activities.imagedistinguish;

import android.os.CountDownTimer;

/**
 * Created by Jason on 2016/8/9.
 */
public class MyCountDownTimer extends CountDownTimer {
    private ICountDownTimerListener listener;

    /**
     * @param millisInFuture    总时长，The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval 时间间隔，The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyCountDownTimer(long millisInFuture, long countDownInterval, ICountDownTimerListener listener) {
        super(millisInFuture, countDownInterval);
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (listener != null) {
            listener.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if (listener != null) {
            listener.onFinish();
        }
    }

    public interface ICountDownTimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
