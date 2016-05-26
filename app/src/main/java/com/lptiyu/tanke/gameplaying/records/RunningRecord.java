package com.lptiyu.tanke.gameplaying.records;

import android.os.Parcel;
import android.os.Parcelable;

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

  int id;

  @SerializedName("index")
  int index;

  @SerializedName("team_id")
  int team_id;

  @SerializedName("create_time")
  long time;

  @SerializedName("x")
  String x = "0";

  @SerializedName("y")
  String y = "0";

  @SerializedName("type")
  RECORD_TYPE type;

  @SerializedName("remark")
  String remark = "";

  private RunningRecord(Builder builder) {
    setId(builder.id);
    setIndex(builder.index);
    setTeam_id(builder.team_id);
    setTime(builder.time);
    setX(builder.x);
    setY(builder.y);
    setType(builder.type);
    setRemark(builder.remark);
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

  public int getTeam_id() {
    return team_id;
  }

  public void setTeam_id(int team_id) {
    this.team_id = team_id;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getX() {
    return x;
  }

  public void setX(String x) {
    this.x = x;
  }

  public String getY() {
    return y;
  }

  public void setY(String y) {
    this.y = y;
  }

  public RECORD_TYPE getType() {
    return type;
  }

  public void setType(RECORD_TYPE type) {
    this.type = type;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public enum RECORD_TYPE implements JsonSerializer<RECORD_TYPE>, JsonDeserializer<RECORD_TYPE> {
    START_GAME(0),
    REACH_POINT(1),
    START_TASK(2),
    FINISH_TASK(3),
    FINISH_GAME(4);

    final int type;

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
            String.format("The item (%d) for GAME_STATE is unexpected.",
                item));
      }
      return FINISH_TASK;
    }

    @Override
    public JsonElement serialize(RECORD_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.type);
    }
  }

  public static final class Builder {
    private int id;
    private int index;
    private int team_id;
    private long time;
    private String x;
    private String y;
    private RECORD_TYPE type;
    private String remark;

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

    public Builder team_id(int val) {
      team_id = val;
      return this;
    }

    public Builder time(long val) {
      time = val;
      return this;
    }

    public Builder x(String val) {
      x = val;
      return this;
    }

    public Builder y(String val) {
      y = val;
      return this;
    }

    public Builder type(RECORD_TYPE val) {
      type = val;
      return this;
    }

    public Builder remark(String val) {
      remark = val;
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
    dest.writeInt(this.team_id);
    dest.writeLong(this.time);
    dest.writeString(this.x);
    dest.writeString(this.y);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeString(this.remark);
  }

  protected RunningRecord(Parcel in) {
    this.id = in.readInt();
    this.index = in.readInt();
    this.team_id = in.readInt();
    this.time = in.readLong();
    this.x = in.readString();
    this.y = in.readString();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : RECORD_TYPE.values()[tmpType];
    this.remark = in.readString();
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
