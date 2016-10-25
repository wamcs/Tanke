package com.lptiyu.tanke.trace.history;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-18
 *         email: wonderfulifeel@gmail.com
 */
public interface IHistoryTrackHelper {

  void onDestroy();

  void queryHistoryTrack(String entityName);

  void queryHistoryTrack(String entityName, int startTime, int endTime);

}
