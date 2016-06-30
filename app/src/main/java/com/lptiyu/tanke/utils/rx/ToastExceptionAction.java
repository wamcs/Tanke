package com.lptiyu.tanke.utils.rx;

import android.content.Context;
import android.widget.Toast;

import rx.functions.Action1;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/6/14
 *
 * @author ldx
 */
public class ToastExceptionAction implements Action1<Throwable> {
  private Context context;

  public ToastExceptionAction(Context context) {
    this.context = context;
  }

  @Override
  public void call(Throwable throwable) {
    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
  }
}
