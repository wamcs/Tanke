package com.lptiyu.tanke.trace.history;

import com.lptiyu.tanke.trace.bean.HistoryTrackData;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-18
 *         email: wonderfulifeel@gmail.com
 */
public interface HistoryTrackCallback {

  void onRequestFailedCallback(String s);

  void onQueryHistoryTrackCallback(HistoryTrackData trackData);
}
