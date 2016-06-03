package com.lptiyu.tanke.gameplaying.pojo;

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
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */
public class Task implements Parcelable {

  private long id;

  @SerializedName("type")
  private TASK_TYPE type;

  private int exp;

  @SerializedName("mission_name")
  private String taskName;

  private String content;

  private String pwd;

  private Task(Builder builder) {
    setId(builder.id);
    setType(builder.type);
    setExp(builder.exp);
    setTaskName(builder.missionName);
    setContent(builder.content);
    setPwd(builder.pwd);
  }

  public enum TASK_TYPE implements JsonSerializer<TASK_TYPE>, JsonDeserializer<TASK_TYPE> {
    SCAN_CODE(0), // scan the QRCode
    LOCATE(1), // locate the position
    RIDDLE(2), // Secret mission
    DISTINGUISH(3), // take the photo and distinguish
    TIMING(4),
    FINISH(5);

    private int type;

    TASK_TYPE(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }


    @Override
    public TASK_TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (TASK_TYPE game_type : TASK_TYPE.values()) {
        if (game_type.getType() == item) {
          return game_type;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for GAME_TYPE is unexpected.",
                item));
      }
      return FINISH;
    }

    @Override
    public JsonElement serialize(TASK_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getType());
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public TASK_TYPE getType() {
    return type;
  }

  public void setType(TASK_TYPE type) {
    this.type = type;
  }

  public int getExp() {
    return exp;
  }

  public void setExp(int exp) {
    this.exp = exp;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
  }


  public static final class Builder {
    private long id;
    private long pointId;
    private TASK_TYPE type;
    private int exp;
    private String missionName;
    private String content;
    private String pwd;

    public Builder() {
    }

    public Builder id(long val) {
      id = val;
      return this;
    }

    public Builder pointId(long val) {
      pointId = val;
      return this;
    }

    public Builder type(TASK_TYPE val) {
      type = val;
      return this;
    }

    public Builder exp(int val) {
      exp = val;
      return this;
    }

    public Builder missionName(String val) {
      missionName = val;
      return this;
    }

    public Builder content(String val) {
      content = val;
      return this;
    }

    public Builder pwd(String val) {
      pwd = val;
      return this;
    }

    public Task build() {
      return new Task(this);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeInt(this.exp);
    dest.writeString(this.taskName);
    dest.writeString(this.content);
    dest.writeString(this.pwd);
  }

  protected Task(Parcel in) {
    this.id = in.readLong();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : TASK_TYPE.values()[tmpType];
    this.exp = in.readInt();
    this.taskName = in.readString();
    this.content = in.readString();
    this.pwd = in.readString();
  }

  public static final Creator<Task> CREATOR = new Creator<Task>() {
    @Override
    public Task createFromParcel(Parcel source) {
      return new Task(source);
    }

    @Override
    public Task[] newArray(int size) {
      return new Task[size];
    }
  };
}
