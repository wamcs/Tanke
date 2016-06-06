package com.lptiyu.tanke.pojo;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is the pojo of game data list item
 * contains the info of every item in the list
 * such as time、exp ect
 */
public class GameDataEntity implements Parcelable {

  private long taskId;

  private long millisConsuming;

  private int exp;

  public long getTaskId() {
    return taskId;
  }

  public void setTaskId(long taskId) {
    this.taskId = taskId;
  }

  public long getMillisConsuming() {
    return millisConsuming;
  }

  public void setMillisConsuming(long millisConsuming) {
    this.millisConsuming = millisConsuming;
  }

  public int getExp() {
    return exp;
  }

  public void setExp(int exp) {
    this.exp = exp;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.taskId);
    dest.writeLong(this.millisConsuming);
    dest.writeInt(this.exp);
  }

  public GameDataEntity() {
  }

  protected GameDataEntity(Parcel in) {
    this.taskId = in.readLong();
    this.millisConsuming = in.readLong();
    this.exp = in.readInt();
  }

  public static final Creator<GameDataEntity> CREATOR = new Creator<GameDataEntity>() {
    @Override
    public GameDataEntity createFromParcel(Parcel source) {
      return new GameDataEntity(source);
    }

    @Override
    public GameDataEntity[] newArray(int size) {
      return new GameDataEntity[size];
    }
  };
}
