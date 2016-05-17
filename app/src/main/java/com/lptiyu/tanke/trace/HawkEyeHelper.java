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
public abstract class HawkEyeHelper {

  protected WeakReference<Context> contextWeakReference;
  protected LBSTraceClient mClient;

  protected final long DEFAULT_SERVICE_ID = 115944L;
  protected final String DEFAULT_ENTITY_NAME = "tanke";

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

  public HawkEyeHelper(Context context) {
    contextWeakReference = new WeakReference<>(context);
    mClient = new LBSTraceClient(contextWeakReference.get());
    mClient.setLocationMode(LocationMode.High_Accuracy);
    mClient.setProtocolType(PROTOCAL_TYPE.HTTP.type);
  }
}
