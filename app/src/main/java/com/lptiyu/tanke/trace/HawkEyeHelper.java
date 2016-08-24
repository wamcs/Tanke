package com.lptiyu.tanke.trace;

import android.content.Context;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;

import java.lang.ref.WeakReference;

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

  public void destroy() {
    if (mClient != null) {

      mClient.onDestroy();
    }
  }

  public static String makeUpTraceEntityName(String gameId, String userId) {
    return gameId + "_" + userId;
  }
}
