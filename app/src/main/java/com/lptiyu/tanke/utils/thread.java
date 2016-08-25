package com.lptiyu.tanke.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ldx
 */
public class thread {

  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
  private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
  private static final int KEEP_ALIVE = 1;

  private static final ThreadFactory sThreadFactory = new ThreadFactory() {
    private final AtomicInteger mCount = new AtomicInteger(1);

    public Thread newThread(Runnable r) {
      return new Thread(r, "thread #" + mCount.getAndIncrement());
    }
  };

  private static final BlockingQueue<Runnable> sPoolWorkQueue =
      new LinkedBlockingQueue<Runnable>(128);

  /**
   * An {@link Executor} that executes tasks one at a time in serial
   * order.  This serialization is global to a particular process.
   */
  private static final Executor SERIAL_EXECUTOR = new SerialExecutor();


  private static class SerialExecutor implements Executor {
    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    Runnable mActive;

    public synchronized void execute(final Runnable r) {
      mTasks.offer(new Runnable() {
        public void run() {
          try {
            r.run();
          } finally {
            scheduleNext();
          }
        }
      });
      if (mActive == null) {
        scheduleNext();
      }
    }

    protected synchronized void scheduleNext() {
      if ((mActive = mTasks.poll()) != null) {
        THREAD_POOL_EXECUTOR.execute(mActive);
      }
    }
  }


  /**
   * An {@link Executor} that can be used to execute tasks in parallel.
   */
  public static final Executor THREAD_POOL_EXECUTOR
      = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
      TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);


  public static Handler mainThreadHandler = new Handler(Looper.getMainLooper());

  private static HandlerThread BACKGROUND_THREAD = new HandlerThread("Background");

  static {
    BACKGROUND_THREAD.start();
  }

  public static Handler backgroundHandler = new Handler(BACKGROUND_THREAD.getLooper());

  public static void background(Runnable runnable) {
    backgroundHandler.post(runnable);
  }

  public static void backgroundDelayed(Runnable runnable, long delayMillis) {
    backgroundHandler.postDelayed(runnable, delayMillis);
  }

  public static void async(Runnable runnable) {
    executeOnExecutor(THREAD_POOL_EXECUTOR, runnable);
  }

  public static void executeOnExecutor(Executor exec, Runnable runnable) {
    exec.execute(runnable);
  }

  public static void mainThread(Runnable runnable) {
    mainThreadHandler.post(runnable);
  }

  public static void mainThreadDelayed(Runnable runnable, long delayMillis) {
    mainThreadHandler.postDelayed(runnable, delayMillis);
  }
}
