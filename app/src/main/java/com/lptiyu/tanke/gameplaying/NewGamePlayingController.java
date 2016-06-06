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
    RecordsUtils.dispatchTypeRecord(34.123123, 114.321321, currentAttackPoint.getId(), 0, RunningRecord.RECORD_TYPE.GAME_START);
    mLoadingDialog.dismiss();
  }
}
