package com.lptiyu.tanke.pojo;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-6
 *         email: wonderfulifeel@gmail.com
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.gameplaying.pojo.Task;

/**
 * This class is the pojo of game data list item
 * contains the info of every item in the list
 * such as time、exp ect
 */
public class GameDataEntity implements Parcelable {

  private String taskName;

  private Task.TASK_TYPE type;

  private int completePersonNum;

  private long completeTime;

  private long completeComsumingTime;

  private int exp;

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Task.TASK_TYPE getType() {
    return type;
  }

  public void setType(Task.TASK_TYPE type) {
    this.type = type;
  }

  public int getCompletePersonNum() {
    return completePersonNum;
  }

  public void setCompletePersonNum(int completePersonNum) {
    this.completePersonNum = completePersonNum;
  }

  public long getCompleteTime() {
    return completeTime;
  }

  public void setCompleteTime(long completeTime) {
    this.completeTime = completeTime;
  }

  public long getCompleteComsumingTime() {
    return completeComsumingTime;
  }

  public void setCompleteComsumingTime(long completeComsumingTime) {
    this.completeComsumingTime = completeComsumingTime;
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
    dest.writeString(this.taskName);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeInt(this.completePersonNum);
    dest.writeLong(this.completeTime);
    dest.writeLong(this.completeComsumingTime);
    dest.writeInt(this.exp);
  }

  public GameDataEntity() {
  }

  protected GameDataEntity(Parcel in) {
    this.taskName = in.readString();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : Task.TASK_TYPE.values()[tmpType];
    this.completePersonNum = in.readInt();
    this.completeTime = in.readLong();
    this.completeComsumingTime = in.readLong();
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
