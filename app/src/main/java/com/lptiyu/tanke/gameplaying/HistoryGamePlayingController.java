package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.gameplaying.pojo.Task;
import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-27
 *         email: wonderfulifeel@gmail.com
 */
public class HistoryGamePlayingController extends GamePlayingController {

  public HistoryGamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
  }

  @Override
  protected void initRecords() {

    currentAttackPoint = mPoints.get(currentAttackPointIndex);
    mapHelper.initMapFlow();

    if (RecordsUtils.isGameStartedFromDisk(gameId) && RecordsUtils.getmRecordsHandler() != null) {
      RecordsUtils.getmRecordsHandler().dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
        @Override
        public void dataResumed(List<RunningRecord> recordList) {
          if (recordList == null) {
            Timber.e("Resume from history records error");
            return;
          }
          resumePointRecords(recordList);
          resumeCurrentPointTaskRecords(recordList);
          mLoadingDialog.dismiss();
        }
      });
    }
  }

  private void resumePointRecords(List<RunningRecord> recordList) {
    recordList = findAppropriateRecords(recordList);
    for (RunningRecord record : recordList) {
      if (record.getPointId() == currentAttackPoint.getId()) {
        switch (record.getType()) {

          case GAME_START:
            if (currentAttackPoint.getPointIndex() == 0) {
              onReachAttackPoint();
              onNextPoint();
            }
            break;

          case POINT_REACH:
            onReachAttackPoint();
            break;

          case POINT_FINISH:
            onNextPoint();
            break;
        }
      } else {
        //TODO : the records are mixed = =, i don't what to do
      }
    }
  }

  /**
   * Resume the current attack point task record
   * in order to avoid user exit app when timing task start
   * according to the task start record, timing task's ending time can be calculate
   *
   * @param recordList all task records relate to this attack point
   */
  private void resumeCurrentPointTaskRecords(List<RunningRecord> recordList) {
    recordList = findCurrentPointTaskRecords(recordList);
    if (recordList != null && recordList.size() != 0) {
      RunningRecord record = recordList.get(recordList.size() - 1);
      if (record == null || record.getType() != RunningRecord.RECORD_TYPE.TASK_START) {
        return;
      }
      long currentTaskId = record.getTaskId();
      Task resultTask = checkIfIsTimingTask(currentTaskId);
      if (resultTask == null) {
        return;
      }
      long startTimeMillis = record.getCreateTime();
      long expectEndTimeMillis = Integer.valueOf(resultTask.getPwd()) * TimeUtils.ONE_MINUTE_TIME + startTimeMillis;
      if (System.currentTimeMillis() < expectEndTimeMillis) {
        mTimingTaskHelper.startTimingTaskFlow(resultTask, startTimeMillis);
      } else {
        mTimingTaskHelper.startTimingTaskFlow(resultTask, Long.MIN_VALUE);
      }
      onNextPoint();
    }
  }

  /**
   * This method is to find records about point reach
   * in the aspect of map, only need to read about point reach record
   *
   * @param records all records from disk file
   * @return the record list about point reach
   */
  private List<RunningRecord> findAppropriateRecords(List<RunningRecord> records) {
    List<RunningRecord> result = new ArrayList<>();
    for (RunningRecord record : records) {
      RunningRecord.RECORD_TYPE type = record.getType();
      if (type == RunningRecord.RECORD_TYPE.GAME_START ||
          type == RunningRecord.RECORD_TYPE.POINT_REACH ||
          type == RunningRecord.RECORD_TYPE.POINT_FINISH) {
        result.add(record);
      }
    }
    return result;
  }

  /**
   * This method is to find all records about current attack point
   *
   * @param records all records
   * @return return the record list
   */
  private List<RunningRecord> findCurrentPointTaskRecords(List<RunningRecord> records) {
    List<RunningRecord> result = new ArrayList<>();
    for (RunningRecord record : records) {
      RunningRecord.RECORD_TYPE type = record.getType();
      if ((record.getPointId() == currentAttackPoint.getId()) && (type == RunningRecord.RECORD_TYPE.TASK_START || type == RunningRecord.RECORD_TYPE.TASK_FINISH)) {
        result.add(record);
      }
    }
    return result;
  }

  /**
   * Check whether the task is timing task
   *
   * @param taskId
   * @return null for false, task instance for true
   */
  private Task checkIfIsTimingTask(long taskId) {
    String taskIdStr = String.valueOf(taskId);
    Task task = currentAttackPoint.getTaskMap().get(taskIdStr);
    if (task == null) {
      return null;
    }
    if (task.getType() == Task.TASK_TYPE.TIMING) {
      return task;
    }
    return null;
  }

}
