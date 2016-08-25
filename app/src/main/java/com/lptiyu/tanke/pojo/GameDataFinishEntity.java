package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-23
 *         email: wonderfulifeel@gmail.com
 */
public class GameDataFinishEntity extends GameDataEntity implements Parcelable {

  private int totalExp;

  private int completeNum;

  private long completeTime;

  private long consumingTime;

  public int getTotalExp() {
    return totalExp;
  }

  public void setTotalExp(int totalExp) {
    this.totalExp = totalExp;
  }

  public int getCompleteNum() {
    return completeNum;
  }

  public void setCompleteNum(int completeNum) {
    this.completeNum = completeNum;
  }

  public long getCompleteTime() {
    return completeTime;
  }

  public void setCompleteTime(long completeTime) {
    this.completeTime = completeTime;
  }

  public long getConsumingTime() {
    return consumingTime;
  }

  public void setConsumingTime(long consumingTime) {
    this.consumingTime = consumingTime;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.totalExp);
    dest.writeInt(this.completeNum);
    dest.writeLong(this.completeTime);
    dest.writeLong(this.consumingTime);
  }

  public GameDataFinishEntity() {
  }

  protected GameDataFinishEntity(Parcel in) {
    this.totalExp = in.readInt();
    this.completeNum = in.readInt();
    this.completeTime = in.readLong();
    this.consumingTime = in.readLong();
  }

  public static final Creator<GameDataFinishEntity> CREATOR = new Creator<GameDataFinishEntity>() {
    @Override
    public GameDataFinishEntity createFromParcel(Parcel source) {
      return new GameDataFinishEntity(source);
    }

    @Override
    public GameDataFinishEntity[] newArray(int size) {
      return new GameDataFinishEntity[size];
    }
  };
}
