package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class GameFinishedEntity implements Parcelable {
  @SerializedName("game_id")
  long gameId;

  @SerializedName("img")
  String img;

  @SerializedName("name")
  String name;

  @SerializedName("start_time")
  String startTime;

  public String getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(String totalTime) {
    this.totalTime = totalTime;
  }

  @SerializedName("time")
  String totalTime;

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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.gameId);
    dest.writeString(this.img);
    dest.writeString(this.name);
    dest.writeString(this.startTime);
    dest.writeString(this.endTime);
    dest.writeString(this.totalTime);
    dest.writeInt(this.type);
    dest.writeInt(this.expPoints);
  }

  public GameFinishedEntity() {
  }

  protected GameFinishedEntity(Parcel in) {
    this.gameId = in.readLong();
    this.img = in.readString();
    this.name = in.readString();
    this.startTime = in.readString();
    this.endTime = in.readString();
    this.totalTime = in.readString();
    this.type = in.readInt();
    this.expPoints = in.readInt();
  }

  public static final Parcelable.Creator<GameFinishedEntity> CREATOR = new Parcelable.Creator<GameFinishedEntity>() {
    @Override
    public GameFinishedEntity createFromParcel(Parcel source) {
      return new GameFinishedEntity(source);
    }

    @Override
    public GameFinishedEntity[] newArray(int size) {
      return new GameFinishedEntity[size];
    }
  };
}
