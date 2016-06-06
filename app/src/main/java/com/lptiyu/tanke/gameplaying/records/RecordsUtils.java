package com.lptiyu.tanke.gameplaying.records;

import java.io.IOException;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RecordsUtils {

  private static RunningRecord.Builder runningRecordBuilder;
  private static RecordsHandler mRecordsHandler;

  private RecordsUtils() {
  }

  public static void initRecordsHandler(RecordsHandler recordsHandler) {
    mRecordsHandler = recordsHandler;
    runningRecordBuilder = new RunningRecord.Builder();
  }

  public static void dispatchTypeRecord(double x, double y, long pointId, long taskId, RunningRecord.RECORD_TYPE type) {
    dispatchTypeRecord(initPointRecord(x, y, pointId, taskId, type));
  }

  public static void dispatchTypeRecord(RunningRecord record) {
    if (mRecordsHandler == null) {
      throw new IllegalStateException("the recordHandler must be init");
    }
    mRecordsHandler.dispatchRunningRecord(record);
  }

  public static boolean isGameFinishedFromMemory(MemRecords memRecords) {
    RunningRecord record = memRecords.last();
    return record != null && record.getType() == RunningRecord.RECORD_TYPE.GAME_FINISH;
  }

  public static boolean isGameStartedFromMemory(MemRecords memRecords) {
    RunningRecord record = memRecords.first();
    return record != null && record.getType() == RunningRecord.RECORD_TYPE.GAME_START;
  }

  /**
   * 把文件中的所有点加载出来，进行计算。重现RunningActivity中的内存场景。
   *
   * @param gameId          决定读取哪个文件
   * @param onlyMetaMessage 如果设为true，读取所有的点，但是不把所有的点都加载到内存中，只需要对这些点进行计算，把计算的结果放在内存中开辟的内存将大大减少。
   * @return 返回记录在内存中的日志
   * @throws IOException
   */
  public static MemRecords loadAllDiskItemIntoMemory(long gameId, boolean onlyMetaMessage) throws IOException {
    DiskRecords diskRecords = new DiskRecords(DiskRecords.generateFile(gameId));
    MemRecords memRecords = new MemRecords();
    memRecords.onlyMeta(onlyMetaMessage);
    diskRecords.fromDisk(memRecords);
    return memRecords;
  }

  /**
   * Used in RecordsHandler not initialized cases.
   *
   * @return whether game started
   */
  public static boolean isGameStartedFromDisk(long gameId) {
    if (gameId == Long.MIN_VALUE) {
      Timber.d("ActivityID is null or is empty string.");
      return false;
    }

    // Now you have the ref of the records in disk.
    DiskRecords records = new DiskRecords(DiskRecords.generateFile(gameId));
    if (records.isEmpty()) {
      Timber.d("DiskRecords is empty, invalid to use.");
      return false;
    }

    RunningRecord record = null;

    try {
      record = records.firstItem();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return record != null && record.getType() == RunningRecord.RECORD_TYPE.GAME_START;
  }

  /**
   * Used in RecordsHandler not initialized cases.
   *
   * @return whether game finished
   */
  public static boolean isGameFinishedFromDisk(long gameId) {
    if (gameId == Long.MIN_VALUE) {
      Timber.d("ActivityID is null or is empty string.");
      return false;
    }

    // Now you have the ref of the records in disk.
    DiskRecords records = new DiskRecords(DiskRecords.generateFile(gameId));
    if (records.isEmpty()) {
      Timber.d("DiskRecords is empty, invalid to use.");
      return false;
    }
    RunningRecord record = null;
    try {
      record = records.lastItem();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ((record != null) && (record.getType() == RunningRecord.RECORD_TYPE.POINT_REACH));
  }

  public static RecordsHandler getmRecordsHandler() {
    return mRecordsHandler;
  }

  private static RunningRecord initPointRecord(double x, double y, long pointId, long taskId, RunningRecord.RECORD_TYPE type) {
    return runningRecordBuilder
        .x(x)
        .y(y)
        .pointId(pointId)
        .taskId(taskId)
        .type(type)
        .createTime(System.currentTimeMillis())
        .build();
  }

}
