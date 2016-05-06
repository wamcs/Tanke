package com.lptiyu.tanke.trace;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public interface TraceCallback {

  void onTraceStart();

  void onTracePush(byte b, String s);

  void onTraceStop();
}
