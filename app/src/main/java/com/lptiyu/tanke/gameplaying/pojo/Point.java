package com.lptiyu.tanke.gameplaying.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */
public class Point implements Parcelable {

  private long id;

  @SerializedName("line_id")
  private long lineId;

  @SerializedName("point_index")
  private int pointIndex;

  private String latitude;

  private String longitude;

  @SerializedName("task_id")
  private List<String> taskId;

  private Map<String, Task> missionMap;

  private Point(Builder builder) {
    setId(builder.id);
    setLineId(builder.lineId);
    setPointIndex(builder.pointIndex);
    setLatitude(builder.latitude);
    setLongitude(builder.longitude);
    setTaskId(builder.taskId);
    setMissionMap(builder.missionMap);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getLineId() {
    return lineId;
  }

  public void setLineId(long lineId) {
    this.lineId = lineId;
  }

  public int getPointIndex() {
    return pointIndex;
  }

  public void setPointIndex(int pointIndex) {
    this.pointIndex = pointIndex;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public List<String> getTaskId() {
    return taskId;
  }

  public void setTaskId(List<String> taskId) {
    this.taskId = taskId;
  }

  public Map<String, Task> getMissionMap() {
    return missionMap;
  }

  public void setMissionMap(Map<String, Task> missionMap) {
    this.missionMap = missionMap;
  }

  public static final class Builder {
    private long id;
    private long lineId;
    private int pointIndex;
    private String latitude;
    private String longitude;
    private List<String> taskId;
    private Map<String, Task> missionMap;

    public Builder() {
    }

    public Builder id(long val) {
      id = val;
      return this;
    }

    public Builder lineId(long val) {
      lineId = val;
      return this;
    }

    public Builder pointIndex(int val) {
      pointIndex = val;
      return this;
    }

    public Builder latitude(String val) {
      latitude = val;
      return this;
    }

    public Builder longitude(String val) {
      longitude = val;
      return this;
    }

    public Builder taskId(List<String> val) {
      taskId = val;
      return this;
    }

    public Builder missionMap(Map<String, Task> val) {
      missionMap = val;
      return this;
    }

    public Point build() {
      return new Point(this);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeLong(this.lineId);
    dest.writeInt(this.pointIndex);
    dest.writeString(this.latitude);
    dest.writeString(this.longitude);
    dest.writeStringList(this.taskId);
    dest.writeInt(this.missionMap.size());
    for (Map.Entry<String, Task> entry : this.missionMap.entrySet()) {
      dest.writeString(entry.getKey());
      dest.writeParcelable(entry.getValue(), flags);
    }
  }

  protected Point(Parcel in) {
    this.id = in.readLong();
    this.lineId = in.readLong();
    this.pointIndex = in.readInt();
    this.latitude = in.readString();
    this.longitude = in.readString();
    this.taskId = in.createStringArrayList();
    int missionMapSize = in.readInt();
    this.missionMap = new HashMap<String, Task>(missionMapSize);
    for (int i = 0; i < missionMapSize; i++) {
      String key = in.readString();
      Task value = in.readParcelable(Task.class.getClassLoader());
      this.missionMap.put(key, value);
    }
  }

  public static final Creator<Point> CREATOR = new Creator<Point>() {
    @Override
    public Point createFromParcel(Parcel source) {
      return new Point(source);
    }

    @Override
    public Point[] newArray(int size) {
      return new Point[size];
    }
  };
}
