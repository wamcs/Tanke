package com.lptiyu.tanke.gameplaying.pojo;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-3
 *         email: wonderfulifeel@gmail.com
 */

/**
 * This enum means the state when this activity finish
 * there are two reasons cause the finish
 * 1. Timing Task
 * 2. User press back button
 */
public enum GAME_ACTIVITY_FINISH_TYPE {
  TIMING_TASK(0),
  USER_ACTION(1);

  int type;

  GAME_ACTIVITY_FINISH_TYPE(int type) {
    this.type = type;
  }

}
