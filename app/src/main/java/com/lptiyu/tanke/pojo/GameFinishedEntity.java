package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class GameFinishedEntity {
  @SerializedName("game_id")
  long gameId;

  @SerializedName("img")
  String img;

  @SerializedName("name")
  String name;

  @SerializedName("start_time")
  String startTime;

  @SerializedName("end_time")
  String endTime;

  int type;

  @SerializedName("values")
  int expPoints;

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getExpPoints() {
    return expPoints;
  }

  public void setExpPoints(int expPoints) {
    this.expPoints = expPoints;
  }
}
