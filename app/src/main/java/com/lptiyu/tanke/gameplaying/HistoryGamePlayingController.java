package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.gameplaying.records.RecordsHandler;
import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;
import com.lptiyu.tanke.global.AppData;

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

    if (RecordsUtils.isGameStartedFromDisk(TEMP_GAME_ID)) {
      mRecordsHandler.dispatchResumeFromDisc(new RecordsHandler.ResumeCallback() {
        @Override
        public void dataResumed(List<RunningRecord> recordList) {
          if (recordList == null) {
            Timber.e("Resume from history records error");
            return;
          }
//          resumeHistoryRecords(recordList);
//          resumeHistoryRecords(mRecordsHandler.getMemRecords().getAll());
          mLoadingDialog.dismiss();
        }
      });
    } else {
      RunningRecord startRecord = new RunningRecord.Builder()
          .x(34.123123)
          .y(114.321321)
          .type(RunningRecord.RECORD_TYPE.GAME_START)
          .pointId(currentAttackPoint.getId())
          .build();
      RecordsUtils.dispatchTypeRecord(mRecordsHandler, startRecord);
      mLoadingDialog.dismiss();
    }

  }

  private void resumeHistoryRecords(List<RunningRecord> recordList) {
    for(RunningRecord r : recordList) {
      Timber.e(AppData.globalGson().toJson(r));
    }
  }

}
