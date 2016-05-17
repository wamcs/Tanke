package com.lptiyu.tanke.trace.realtime;

import android.content.Context;

import com.baidu.trace.OnEntityListener;
import com.lptiyu.tanke.trace.HawkEyeHelper;
import com.lptiyu.tanke.trace.bean.HistoryTrackData;
import com.lptiyu.tanke.utils.GsonUtil;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-17
 *         email: wonderfulifeel@gmail.com
 */
public class RealTimeTrackHelper extends HawkEyeHelper implements
    IRealTimeTrackHelper {

  private OnEntityListener entityListener;

  private RealTimeTrackCallback mCallback;

  private final String DEFAULT_COLUMN_KEY = "";
  private final int DEFAULT_PAGE_SIZE = 1000;
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
    mCallback = callback;
    entityListener = new OnEntityListener() {
      @Override
      public void onRequestFailedCallback(String s) {
        mCallback.onRequestFailedCallback(s);
      }

      @Override
      public void onQueryEntityListCallback(String s) {
        HistoryTrackData historyTrackData = GsonUtil.parseJson(s,
            HistoryTrackData.class);
        mCallback.onQueryEntityListCallback(historyTrackData);
      }
    };
  }

  public void queryEntityList(String entityNames) {
    queryEntityList(entityNames, DEFAULT_COLUMN_KEY);
  }

  public void queryEntityList(String entityNames, String columnKey) {
    int activeTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
    queryEntityList(DEFAULT_SERVICE_ID, entityNames, columnKey, RETURN_TYPE.ONLY_ENTITY.type, activeTime, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_INDEX);
  }

  private void queryEntityList(long serviceId, String entityNames, String columnKey, int returnType, int activeTime, int pageSize, int pageIndex) {
    mClient.queryEntityList(serviceId, entityNames, columnKey, returnType, activeTime, pageSize,
        pageIndex, entityListener);
  }

}
