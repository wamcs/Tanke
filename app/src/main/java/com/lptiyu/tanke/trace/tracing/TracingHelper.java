package com.lptiyu.tanke.trace.tracing;

import android.content.Context;

import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.lptiyu.tanke.trace.HawkEyeHelper;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-17
 *         email: wonderfulifeel@gmail.com
 */
public class TracingHelper extends HawkEyeHelper implements
    ITracingHelper,
    OnStartTraceListener,
    OnStopTraceListener {

  private Trace mTrace;

  private TracingCallback mCallback;

  private boolean isTracing = false;

  private final int DEFAULT_GATER_INTERVAL = 2;
  private final int DEFAULT_PACK_INTERVAL = 10;
  private final int DEFAULT_TRACE_TYPE = 2;

  public TracingHelper(Context context, TracingCallback callback) {
    super(context);
    mTrace = new Trace(contextWeakReference.get(), DEFAULT_SERVICE_ID, DEFAULT_ENTITY_NAME, DEFAULT_TRACE_TYPE);
    mClient.setInterval(DEFAULT_GATER_INTERVAL, DEFAULT_PACK_INTERVAL);
    mCallback = callback;
  }


  @Override
  public void start() {
    //TODO : set the entityName before invoke start
    mClient.startTrace(mTrace, this);
  }

  @Override
  public void stop() {
    mClient.stopTrace(mTrace, this);
  }

  @Override
  public void onTraceCallback(int i, String s) {
    isTracing = true;
    mCallback.onTraceStart();
  }

  @Override
  public void onTracePushCallback(byte b, String s) {
    mCallback.onTracePush(b, s);
  }

  @Override
  public void onStopTraceSuccess() {
    isTracing = false;
    mCallback.onTraceStop();
  }

  @Override
  public void onStopTraceFailed(int i, String s) {
    Timber.d(s);
  }

  public void serviceId(long serviceId) {
    if (isTracing) {
      return;
    }
    mTrace.setServiceId(serviceId);
  }

  public void entityName(String entityName) {
    if (isTracing) {
      return;
    }
    mTrace.setEntityName(entityName);
  }

  public void interval(int gatherInterval, int packInterval) {
    if (isTracing) {
      return;
    }
    mClient.setInterval(gatherInterval, packInterval);
  }
}
