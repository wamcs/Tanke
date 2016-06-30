package com.lptiyu.tanke.gameplaying.task;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.utils.Inflater;

import java.lang.ref.WeakReference;


/**
 * @author : xiaoxiaoda
 *         date: 16-6-3
 *         email: wonderfulifeel@gmail.com
 */
class TimingCounter extends CountDownTimer {

  private AlertDialog timingDialog;
  private TextView mTimingText;

  private OnCounterFinishedListener listener;

  private WeakReference<Context> contextWeakReference;

  public TimingCounter(Context context, long millisInFuture, long countDownInterval) {
    super(millisInFuture, countDownInterval);
    contextWeakReference = new WeakReference<>(context);
    View view = Inflater.inflate(R.layout.layout_dialog_timing_task, null, false);
    mTimingText = (TextView) view.findViewById(R.id.layout_dialog_timing_task_text);
    timingDialog = new AlertDialog.Builder(contextWeakReference.get())
        .setCancelable(false)
        .setView(view)
        .create();
  }

  public void startCounting() {
    timingDialog.show();
    start();
  }


  @Override
  public void onTick(long millisUntilFinished) {
    if (mTimingText != null) {
      mTimingText.setText(String.format(contextWeakReference.get().getString(R.string.timing_dialog_message_formatter), (int) millisUntilFinished / 1000));
    }
  }

  @Override
  public void onFinish() {
    timingDialog.dismiss();
    if (listener != null) {
      listener.onCounterFinished();
    }
  }

  public void setListener(OnCounterFinishedListener listener) {
    this.listener = listener;
  }

  interface OnCounterFinishedListener {
    void onCounterFinished();
  }

}
