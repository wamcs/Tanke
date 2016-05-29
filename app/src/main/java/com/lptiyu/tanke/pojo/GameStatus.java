package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * 游戏当前的完成情况
 *
 * @author ldx
 */
public class GameStatus {

  //经验值
  @SerializedName("values")
  int expPoints;

  @SerializedName("num")
  int finishedTask;

  //游戏开始时间
  @SerializedName("start_time")
  int startTime;

  //Json 字段名称就是这样，这里没有错
  @SerializedName("duration_time")
  int durationTime;

  @SerializedName("data")
  List<TaskStatus> tasks;

  public class TaskStatus{
    @SerializedName("game_id")
    long gameId;

    @SerializedName("title")
    String title;

    @SerializedName("time")
    String time;

    @SerializedName("values")
    int expPoints;

    public long getGameId() {
      return gameId;
    }

    public void setGameId(long gameId) {
      this.gameId = gameId;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }

    public int getExpPoints() {
      return expPoints;
    }

    public void setExpPoints(int expPoints) {
      this.expPoints = expPoints;
    }
  }

  public int getExpPoints() {
    return expPoints;
  }

  public void setExpPoints(int expPoints) {
    this.expPoints = expPoints;
  }

  public int getFinishedTask() {
    return finishedTask;
  }

  public void setFinishedTask(int finishedTask) {
    this.finishedTask = finishedTask;
  }

  public int getStartTime() {
    return startTime;
  }

  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  public int getDurationTime() {
    return durationTime;
  }

  public void setDurationTime(int durationTime) {
    this.durationTime = durationTime;
  }

  public List<TaskStatus> getTasks() {
    return tasks;
  }

  public void setTasks(List<TaskStatus> tasks) {
    this.tasks = tasks;
  }
}
