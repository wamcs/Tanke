package com.lptiyu.tanke.trace;

import android.content.Context;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public class TraceHelper implements
    ITraceHelper,
    OnStartTraceListener,
    OnStopTraceListener {

  private WeakReference<Context> contextWeakReference;
  private LBSTraceClient mClient;
  private Trace mTrace;

  private boolean isTracing = false;

  private final int DEFAULT_TRACE_TYPE = 2;
  private final long DEFAULT_SERVICE_ID = 115944L;
  private final String DEFAULT_ENTITY_NAME = "tanke";

  private TraceCallback mCallback;

  enum PROTOCAL_TYPE {
    HTTP(0),
    HTTPS(1);

    int type;

    PROTOCAL_TYPE(int type) {
      this.type = type;
    }

    public void setType(int type) {
      this.type = type;
    }
  }

  public TraceHelper(Context context, TraceCallback callback) {
    contextWeakReference = new WeakReference<>(context);
    mTrace = new Trace(contextWeakReference.get(), DEFAULT_SERVICE_ID, DEFAULT_ENTITY_NAME, DEFAULT_TRACE_TYPE);
    mClient = new LBSTraceClient(contextWeakReference.get());
    mClient.setLocationMode(LocationMode.High_Accuracy);
    mClient.setProtocolType(PROTOCAL_TYPE.HTTP.type);
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
