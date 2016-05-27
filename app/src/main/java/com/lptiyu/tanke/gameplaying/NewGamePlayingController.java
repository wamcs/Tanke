package com.lptiyu.tanke.gameplaying;

import android.support.v7.app.AppCompatActivity;
import android.view.View;


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
  }
}
