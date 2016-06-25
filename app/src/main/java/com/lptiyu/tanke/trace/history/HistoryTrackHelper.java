package com.lptiyu.tanke.trace.history;

import android.content.Context;

import com.baidu.trace.OnTrackListener;
import com.lptiyu.tanke.global.AppData;
import com.lptiyu.tanke.trace.HawkEyeHelper;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-18
 *         email: wonderfulifeel@gmail.com
 */
public class HistoryTrackHelper extends HawkEyeHelper implements
    IHistoryTrackHelper {

  enum IS_SIMPLE {
    FALSE(0),
    TRUE(1);

    private int type;

    IS_SIMPLE(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
  }

  enum IS_PROCESSED {
    FALSE(0),
    TRUE(1);

    private int type;

    IS_PROCESSED(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }
  }

  private OnTrackListener trackListener;

  private WeakReference<HistoryTrackCallback> mCallbackReference;

  private final int DEFAULT_IS_SIMPLE_RETURN = 0;
  private final int DEFAULT_IS_PROCESSED = 1;
  private final int DEFAULT_PAGE_SIZE = 1000;
  private final int DEFAULT_PAGE_INDEX = 1;

  public HistoryTrackHelper(Context context, HistoryTrackCallback callback) {
    super(context);
    mCallbackReference = new WeakReference<>(callback);
    trackListener = new OnTrackListener() {
      @Override
      public void onRequestFailedCallback(String s) {
        if (mCallbackReference.get() != null) {
          mCallbackReference.get().onRequestFailedCallback(s);
        }
      }

      @Override
      public void onQueryHistoryTrackCallback(String s) {
        HistoryTrackData data = AppData.globalGson().fromJson(s, HistoryTrackData.class);
        if (mCallbackReference.get() != null) {
          mCallbackReference.get().onQueryHistoryTrackCallback(data);
        }
      }
    };
  }

  @Override
  public void queryHistoryTrack(String entityName) {
    int startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
    int endTime = (int) (System.currentTimeMillis() / 1000);
    queryHistoryTrack(entityName, startTime, endTime);
  }

  @Override
  public void queryHistoryTrack(String entityName, int startTimeSecond, int endTimeSecond) {
    queryProcessedHistoryTrack(DEFAULT_SERVICE_ID, entityName, DEFAULT_IS_SIMPLE_RETURN, DEFAULT_IS_PROCESSED, startTimeSecond, endTimeSecond, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
  }

  private void queryProcessedHistoryTrack(long serviceId, String entityName, int simpleReturn, int isProcessed, int startTime, int endTime, int pageSize, int pageIndex) {
    Timber.e("start : %d, end : %d", startTime, endTime);
    mClient.queryProcessedHistoryTrack(
        serviceId,
        entityName,
        simpleReturn,
        isProcessed,
        startTime, endTime,
        pageSize,
        pageIndex,
        trackListener);
  }

  @Override
  public void onDestroy() {
    super.destroy();
  }
}
