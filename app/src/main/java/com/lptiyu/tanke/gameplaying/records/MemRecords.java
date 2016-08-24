package com.lptiyu.tanke.gameplaying.records;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class MemRecords implements Parcelable {

  public static final String MEMORY_RECORD_KEY = "MEMORY_RECORDS_KEY";

  private List<RunningRecord> recordArrayList = new ArrayList<>();

  private boolean onlyMeta = false;

  public MemRecords() {

  }

  public boolean isOnlyMeta() {
    return onlyMeta;
  }

  public void onlyMeta(boolean onlyMeta) {
    this.onlyMeta = onlyMeta;
  }

  public MemRecords(List<RunningRecord> recordList) {
    recordArrayList = recordList;
  }

  public void add(RunningRecord record) {
    if (record == null) {
      return;
    }
    synchronized (this) {
      if (!onlyMeta) {
        recordArrayList.add(record);
      }
    }
  }

  public void addAll(List<RunningRecord> recordList) {
    if (recordList == null || recordList.size() == 0) {
      return;
    }

    synchronized (this) {
      if (!onlyMeta) {
        recordArrayList.addAll(recordList);
      }
    }
  }

  public void clear() {
    synchronized (this) {
      if (!onlyMeta) {
        recordArrayList.clear();
      }
    }
  }

  public int size() {
    synchronized (this) {
      return recordArrayList.size();
    }
  }

  public RunningRecord first() {
    if (recordArrayList == null || recordArrayList.size() == 0) {
      return null;
    }
    return recordArrayList.get(0);
  }

  public RunningRecord last() {
    if (recordArrayList == null || recordArrayList.size() == 0) {
      return null;
    }

    return recordArrayList.get(recordArrayList.size() - 1);
  }


  public List<RunningRecord> getAll() {
    return recordArrayList;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeTypedList(recordArrayList);
    dest.writeByte(onlyMeta ? (byte) 1 : (byte) 0);
  }

  protected MemRecords(Parcel in) {
    this.recordArrayList = in.createTypedArrayList(RunningRecord.CREATOR);
    this.onlyMeta = in.readByte() != 0;
  }

  public static final Creator<MemRecords> CREATOR = new Creator<MemRecords>() {
    public MemRecords createFromParcel(Parcel source) {
      return new MemRecords(source);
    }

    public MemRecords[] newArray(int size) {
      return new MemRecords[size];
    }
  };
}
