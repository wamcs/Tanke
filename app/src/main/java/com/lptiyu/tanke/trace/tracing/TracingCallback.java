package com.lptiyu.tanke.trace.tracing;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public interface TracingCallback {

  void onTraceStart();

  void onTracePush(byte b, String s);

  void onTraceStop();
}
