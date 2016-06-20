package com.lptiyu.tanke.trace.tracing;


/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public interface ITracingHelper {

  void start();

  boolean isStarted();

  void stop();

  void onDestroy();

  void interval(int gatherInterval, int packInterval);

  void serviceId(long serviceId);

  void entityName(String entityName);
}
