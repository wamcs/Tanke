package com.lptiyu.tanke.gameplaying.records;

import com.google.gson.Gson;

import java.io.IOException;

import timber.log.Timber;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RecordsUtils {

  private RecordsUtils() {
  }

  public static RunningRecord generateTypeRecords(RecordsHandler handler, RunningRecord.RECORD_TYPE RECORDTYPE) {
    return new RunningRecord.Builder()
        .type(RECORDTYPE)
        .time(System.currentTimeMillis())
        .build();
  }

  public static void dispatchTypeRecords(RecordsHandler handler, RunningRecord.RECORD_TYPE RECORDTYPE) {
    handler.dispatchRunningRecord(generateTypeRecords(handler, RECORDTYPE));
  }

  // 记录用户到达了某个点的信息，能够把对某一个点标示到达, 这个信息会被放在Message里，这是比较危险的，但是现在赶工期，暂时只能这样了。
  public static void dispatchTypeReachedSpot(RecordsHandler handler, int index) {
    RunningRecord record = new RunningRecord.Builder()
        .type(RunningRecord.RECORD_TYPE.ON_POINT_REACHED)
        .time(System.currentTimeMillis())
        .remark("" + index)
        .build();
    handler.dispatchRunningRecord(record);
  }

  public static RunningRecord generateNormalRecords(RecordsHandler handler, String x, String y) {
    return new RunningRecord.Builder()
        .team_id(handler.teamId)
        .x(x)
        .y(y)
        .type(RunningRecord.RECORD_TYPE.NORMAL)
        .time(System.currentTimeMillis())
        .build();
  }

  public static void dispatchNormalRecords(RecordsHandler handler, String x, String y) {
    handler.dispatchRunningRecord(generateNormalRecords(handler, x, y));
  }

  public static boolean isGameFinishedFromMemory(MemRecords memRecords) {
    RunningRecord record = memRecords.last();
    return record != null && record.getType() == RunningRecord.RECORD_TYPE.ON_FINISH;
  }

  public static boolean isGameStartedFromMemory(MemRecords memRecords) {
    RunningRecord record = memRecords.first();
    return record != null && record.getType() == RunningRecord.RECORD_TYPE.ON_START;
  }

  /**
   * 把文件中的所有点加载出来，进行计算。重现RunningActivity中的内存场景。
   *
   * @param activityID      决定读取哪个文件
   * @param onlyMetaMessage 如果设为true，读取所有的点，但是不把所有的点都加载到内存中，只需要对这些点进行计算，把计算的结果放在内存中开辟的内存将大大减少。
   * @return 返回记录在内存中的日志
   * @throws IOException
   */
  public static MemRecords loadAllDiskItemIntoMemory(String activityID, boolean onlyMetaMessage) throws IOException {
    DiskRecords diskRecords = new DiskRecords(DiskRecords.generateFile(activityID));
    MemRecords memRecords = new MemRecords();
    memRecords.onlyMeta(onlyMetaMessage);
    diskRecords.fromDisk(memRecords);
    return memRecords;
  }

  public static void writeMetaMessage(String activityID, QMetaMessage message) {
    try {
      message.save(DiskRecords.generateMetaFile(activityID));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static QMetaMessage readMetaMessage(String activityID) {
    QMetaMessage metaMessage = null;

    try {
      metaMessage = QMetaMessage.read(DiskRecords.generateMetaFile(activityID));
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return metaMessage;
  }

  /**
   * Used in RecordsHandler not initialized cases.
   *
   * @return whether game started
   */
  public static boolean isGameStartedFromDisk(String activity_id) {
    if (activity_id == null || activity_id.length() == 0) {
      Timber.d("ActivityID is null or is empty string.");
      return false;
    }

    // Now you have the ref of the records in disk.
    DiskRecords records = new DiskRecords(DiskRecords.generateFile(activity_id));
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

    return record != null && record.getType() == RunningRecord.RECORD_TYPE.ON_START;
  }

  /**
   * Used in RecordsHandler not initialized cases.
   *
   * @return whether game finished
   */
  public static boolean isGameFinishedFromDisk(String activity_id) {
    if (activity_id == null || activity_id.length() == 0) {
      Timber.d("ActivityID is null or is empty string.");
      return false;
    }

    // Now you have the ref of the records in disk.
    DiskRecords records = new DiskRecords(DiskRecords.generateFile(activity_id));
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

    Timber.e("record : " + new Gson().toJson(record));
    Timber.e("isfinished " + (record != null && (record.getType() == RunningRecord.RECORD_TYPE.ON_FINISH)));
    return ((record != null) && (record.getType() == RunningRecord.RECORD_TYPE.ON_FINISH));
  }

}
