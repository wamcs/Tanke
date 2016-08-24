package com.lptiyu.tanke.trace.realtime;

import com.lptiyu.tanke.trace.bean.HistoryTrackData;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-17
 *         email: wonderfulifeel@gmail.com
 */
public interface RealTimeTrackCallback {

  public void onRequestFailedCallback(String s);

  public void onQueryEntityListCallback(HistoryTrackData trackDataList);

}
