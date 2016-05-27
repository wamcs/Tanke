package com.lptiyu.tanke.trace.realtime;

import android.content.Context;

import com.baidu.trace.OnEntityListener;
import com.baidu.trace.TraceLocation;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.trace.HawkEyeHelper;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-17
 *         email: wonderfulifeel@gmail.com
 */
public class RealTimeTrackHelper extends HawkEyeHelper implements
    IRealTimeTrackHelper {

  private OnEntityListener entityListener;

  private WeakReference<RealTimeTrackCallback> mCallbackReference;

  private final String DEFAULT_COLUMN_KEY = "";
  private final int DEFAULT_PAGE_SIZE = 10;
  private final int DEFAULT_PAGE_INDEX = 1;

  enum RETURN_TYPE {
    ALL_TARGET(0),
    ONLY_ENTITY(1);

    private int type;

    RETURN_TYPE(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
  }

  public RealTimeTrackHelper(Context context, RealTimeTrackCallback callback) {
    super(context);
    mCallbackReference = new WeakReference<>(callback);
    entityListener = new OnEntityListener() {
      @Override
      public void onRequestFailedCallback(String s) {
        if (mCallbackReference.get() != null) {
          mCallbackReference.get().onRequestFailedCallback(s);
        }
      }

      @Override
      public void onQueryEntityListCallback(String s) {
        HistoryTrackData historyTrackData = AppData.globalGson().fromJson(s,
            HistoryTrackData.class);
        if (mCallbackReference.get() != null) {
          mCallbackReference.get().onQueryEntityListCallback(historyTrackData);
        }
      }

      @Override
      public void onReceiveLocation(TraceLocation traceLocation) {
        Timber.e(traceLocation.toString());
      }
    };
  }

  public void queryEntityList(String entityNames) {
    queryEntityList(entityNames, DEFAULT_COLUMN_KEY);
  }



  public void queryEntityList(String entityNames, String columnKey) {
    int activeTime = (int) (System.currentTimeMillis() / 1000 - 30 * 60);
    queryEntityList(DEFAULT_SERVICE_ID, entityNames, columnKey, RETURN_TYPE.ALL_TARGET.type, activeTime, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
  }

  private void queryEntityList(long serviceId, String entityNames, String columnKey, int returnType, int activeTime, int pageSize, int pageIndex) {
    mClient.queryEntityList(serviceId, entityNames, columnKey, returnType, activeTime, pageSize,
        pageIndex, entityListener);
  }

  @Override
  public void onDestroy() {
    super.destroy();
  }

}
