package com.lptiyu.tanke.gameplaying.records;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.lptiyu.tanke.BuildConfig;

import java.lang.reflect.Type;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RunningRecord implements Parcelable {

  private int id;

  private int index;

  @SerializedName("team_id")
  private long teamId;

  @SerializedName("create_time")
  private long createTime;

  private int distance;

  private double x;

  private double y;

  @SerializedName("type")
  private RECORD_TYPE type;

  @SerializedName("point_id")
  private long pointId;

  @SerializedName("task_id")
  private long taskId;

  private RunningRecord(Builder builder) {
    setId(builder.id);
    setIndex(builder.index);
    setTeamId(builder.teamId);
    setCreateTime(builder.createTime);
    setDistance(builder.distance);
    setX(builder.x);
    setY(builder.y);
    setType(builder.type);
    setPointId(builder.pointId);
    setTaskId(builder.taskId);
  }

  public enum RECORD_TYPE implements JsonSerializer<RECORD_TYPE>, JsonDeserializer<RECORD_TYPE> {
    GAME_START(0),
    POINT_REACH(1),
    TASK_START(2),
    TASK_FINISH(3),
    GAME_FINISH(4);

    public final int type;

    public int getType() {
      return type;
    }

    RECORD_TYPE(int i) {
      type = i;
    }

    @Override
    public RECORD_TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (RECORD_TYPE state : RECORD_TYPE.values()) {
        if (state.getType() == item) {
          return state;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for RECORD_TYPE is unexpected.",
                item));
      }
      return TASK_FINISH;
    }

    @Override
    public JsonElement serialize(RECORD_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.type);
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public RECORD_TYPE getType() {
    return type;
  }

  public void setType(RECORD_TYPE type) {
    this.type = type;
  }

  public long getPointId() {
    return pointId;
  }

  public void setPointId(long pointId) {
    this.pointId = pointId;
  }

  public long getTaskId() {
    return taskId;
  }

  public void setTaskId(long taskId) {
    this.taskId = taskId;
  }

  public LatLng getLatLng() {
    return new LatLng(x, y);
  }

  public static final class Builder {
    private int id;
    private int index;
    private int teamId;
    private long createTime;
    private int distance;
    private double x;
    private double y;
    private RECORD_TYPE type;
    private long pointId;
    private long taskId;

    public Builder() {
    }

    public Builder id(int val) {
      id = val;
      return this;
    }

    public Builder index(int val) {
      index = val;
      return this;
    }

    public Builder teamId(int val) {
      teamId = val;
      return this;
    }

    public Builder createTime(long val) {
      createTime = val;
      return this;
    }

    public Builder distance(int val) {
      distance = val;
      return this;
    }

    public Builder x(double val) {
      x = val;
      return this;
    }

    public Builder y(double val) {
      y = val;
      return this;
    }

    public Builder type(RECORD_TYPE val) {
      type = val;
      return this;
    }

    public Builder pointId(long val) {
      pointId = val;
      return this;
    }

    public Builder taskId(long val) {
      taskId = val;
      return this;
    }

    public RunningRecord build() {
      return new RunningRecord(this);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeInt(this.index);
    dest.writeLong(this.teamId);
    dest.writeLong(this.createTime);
    dest.writeInt(this.distance);
    dest.writeDouble(this.x);
    dest.writeDouble(this.y);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeLong(this.pointId);
    dest.writeLong(this.taskId);
  }

  protected RunningRecord(Parcel in) {
    this.id = in.readInt();
    this.index = in.readInt();
    this.teamId = in.readLong();
    this.createTime = in.readLong();
    this.distance = in.readInt();
    this.x = in.readDouble();
    this.y = in.readDouble();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : RECORD_TYPE.values()[tmpType];
    this.pointId = in.readLong();
    this.taskId = in.readLong();
  }

  public static final Creator<RunningRecord> CREATOR = new Creator<RunningRecord>() {
    @Override
    public RunningRecord createFromParcel(Parcel source) {
      return new RunningRecord(source);
    }

    @Override
    public RunningRecord[] newArray(int size) {
      return new RunningRecord[size];
    }
  };
}
