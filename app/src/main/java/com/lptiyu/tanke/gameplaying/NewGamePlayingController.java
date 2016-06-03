package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lptiyu.tanke.gameplaying.records.RecordsUtils;
import com.lptiyu.tanke.gameplaying.records.RunningRecord;


/**
 * @author : xiaoxiaoda
 *         date: 16-5-27
 *         email: wonderfulifeel@gmail.com
 */
public class NewGamePlayingController extends GamePlayingController {

  public NewGamePlayingController(AppCompatActivity activity, View view) {
    super(activity, view);
  }

  @Override
  protected void initRecords() {
    currentAttackPoint = mPoints.get(currentAttackPointIndex);
    mapHelper.initMapFlow();
    dispatchGameStartRecord();
    mLoadingDialog.dismiss();
  }

  private void dispatchGameStartRecord() {
    RunningRecord startRecord = new RunningRecord.Builder()
        .x(34.123123)
        .y(114.321321)
        .type(RunningRecord.RECORD_TYPE.GAME_START)
        .pointId(currentAttackPoint.getId())
        .build();
    RecordsUtils.dispatchTypeRecord(mRecordsHandler, startRecord);
  }
}
