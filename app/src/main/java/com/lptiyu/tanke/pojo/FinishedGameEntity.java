package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class FinishedGameEntity {
  @SerializedName("game_id")
  long gameId;

  @SerializedName("img")
  String img;

  @SerializedName("name")
  String name;

  @SerializedName("time")
  int time; //TODO 单位不明确

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

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getExpPoints() {
    return expPoints;
  }

  public void setExpPoints(int expPoints) {
    this.expPoints = expPoints;
  }
}
