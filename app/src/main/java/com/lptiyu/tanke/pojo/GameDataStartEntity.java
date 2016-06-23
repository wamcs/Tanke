package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-23
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataStartEntity extends GameDataEntity implements Parcelable {

  private long gameId;

  private String gameImage;

  private String gameLoc;

  private int finishedNum;

  private long startTime;

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }

  public String getGameImage() {
    return gameImage;
  }

  public void setGameImage(String gameImage) {
    this.gameImage = gameImage;
  }

  public String getGameLoc() {
    return gameLoc;
  }

  public void setGameLoc(String gameLoc) {
    this.gameLoc = gameLoc;
  }

  public int getFinishedNum() {
    return finishedNum;
  }

  public void setFinishedNum(int finishedNum) {
    this.finishedNum = finishedNum;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.gameId);
    dest.writeString(this.gameImage);
    dest.writeString(this.gameLoc);
    dest.writeInt(this.finishedNum);
    dest.writeLong(this.startTime);
  }

  public GameDataStartEntity() {
  }

  protected GameDataStartEntity(Parcel in) {
    this.gameId = in.readLong();
    this.gameImage = in.readString();
    this.gameLoc = in.readString();
    this.finishedNum = in.readInt();
    this.startTime = in.readLong();
  }

  public static final Creator<GameDataStartEntity> CREATOR = new Creator<GameDataStartEntity>() {
    @Override
    public GameDataStartEntity createFromParcel(Parcel source) {
      return new GameDataStartEntity(source);
    }

    @Override
    public GameDataStartEntity[] newArray(int size) {
      return new GameDataStartEntity[size];
    }
  };
}
