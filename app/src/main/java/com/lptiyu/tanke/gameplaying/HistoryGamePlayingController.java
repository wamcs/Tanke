package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;

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

    if (RecordsUtils.isGameStartedFromDisk(gameId)) {
      mRecordsHandler.dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
        @Override
        public void dataResumed(List<RunningRecord> recordList) {
          if (recordList == null) {
            Timber.e("Resume from history records error");
            return;
          }
          resumeHistoryRecords(recordList);
          mLoadingDialog.dismiss();
        }
      });
    }
  }

  private void resumeHistoryRecords(List<RunningRecord> recordList) {
    recordList = findAppropriateRecords(recordList);
    for (RunningRecord record : recordList) {
      if (record.getPointId() == currentAttackPoint.getId()) {
        switch (record.getType()) {
          case POINT_REACH:
            mapHelper.onReachAttackPoint(currentAttackPointIndex);
            consoleHelper.onReachAttackPoint(currentAttackPointIndex);
            onReachAttackPoint();
            break;

          case POINT_FINISH:
            onNextPoint();
            break;
        }
      } else {

      }
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
      if (type == RunningRecord.RECORD_TYPE.POINT_REACH || type == RunningRecord.RECORD_TYPE.POINT_FINISH) {
        result.add(record);
      }
    }
    return result;
  }

}
