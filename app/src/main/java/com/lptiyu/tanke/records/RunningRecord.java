package com.lptiyu.tanke.records;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RunningRecord implements Parcelable {

  @SerializedName("index")
  int index;

  @SerializedName("team_id")
  int team_id;

  @SerializedName("time")
  long time;

  @SerializedName("x")
  String x = "0";

  @SerializedName("y")
  String y = "0";

  @SerializedName("type")
  int type;

  @SerializedName("message")
  String message = "";

  @SerializedName("is_master")
  boolean is_master;

  private RunningRecord(Builder builder) {
    setTeam_id(builder.team_id);
    setTime(builder.time);
    setX(builder.x);
    setY(builder.y);
    setType(builder.type);
    setMessage(builder.message);
    setIs_master(builder.is_master);
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

  public int getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type.type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean is_master() {
    return is_master;
  }

  public void setIs_master(boolean is_master) {
    this.is_master = is_master;
  }

  public RunningRecord() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.index);
    dest.writeInt(this.team_id);
    dest.writeLong(this.time);
    dest.writeString(this.x);
    dest.writeString(this.y);
    dest.writeInt(this.type);
    dest.writeString(this.message);
    dest.writeByte(is_master ? (byte) 1 : (byte) 0);
  }

  protected RunningRecord(Parcel in) {
    this.index = in.readInt();
    this.team_id = in.readInt();
    this.time = in.readLong();
    this.x = in.readString();
    this.y = in.readString();
    this.type = in.readInt();
    this.message = in.readString();
    this.is_master = in.readByte() != 0;
  }

  public static final Creator<RunningRecord> CREATOR = new Creator<RunningRecord>() {
    public RunningRecord createFromParcel(Parcel source) {
      return new RunningRecord(source);
    }

    public RunningRecord[] newArray(int size) {
      return new RunningRecord[size];
    }
  };

  public static final class Builder {
    private int team_id;
    private long time;
    private String x = "0";
    private String y = "0";
    private Type type;
    private String message = "";
    private boolean is_master = false;

    public Builder() {
    }

    public Builder withTeam_id(int val) {
      team_id = val;
      return this;
    }

    public Builder withTime(long val) {
      time = val;
      return this;
    }

    public Builder withX(String val) {
      x = val;
      return this;
    }

    public Builder withY(String val) {
      y = val;
      return this;
    }

    public Builder withType(Type val) {
      type = val;
      return this;
    }

    public Builder withMessage(String val) {
      message = val;
      return this;
    }

    public Builder withIs_master(boolean val) {
      is_master = val;
      return this;
    }

    public RunningRecord build() {
      return new RunningRecord(this);
    }
  }

  public enum Type {
    Normal(0),
    OnStart(1),
    OnFinish(2),
    OnFailed(3),
    OnSpotReached(4),
    OnSpotCompleted(5);

    final int type;

    public int getType() {
      return type;
    }

    Type(int i) {
      type = i;
    }
  }
}
