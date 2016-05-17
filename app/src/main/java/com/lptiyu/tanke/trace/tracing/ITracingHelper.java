package com.lptiyu.tanke.trace.tracing;


/**
 * @author : xiaoxiaoda
 *         date: 16-5-6
 *         email: wonderfulifeel@gmail.com
 */
public interface ITracingHelper {

  void start();

  void stop();

  void interval(int gatherInterval, int packInterval);

  void serviceId(long serviceId);

  void entityName(String entityName);
}
