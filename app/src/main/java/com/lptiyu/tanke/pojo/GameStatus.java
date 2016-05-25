package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * 游戏当前的完成情况
 *
 * @author ldx
 */
public class GameStatus {
  //耗时
  int time;//TODO 耗时和durationTime有重叠部分。

  //经验值
  @SerializedName("values")
  int expPoints;

  @SerializedName("num")
  int finishedTask;

  //游戏开始时间
  @SerializedName("get_time")
  int startTime;

  //Json 字段名称就是这样，这里没有错
  @SerializedName("Start_time")
  int durationTime;

}
