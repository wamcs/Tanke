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
public class ThemeLine implements Parcelable {

  private long id;

  @SerializedName("game_id")
  private long gameId;

  @SerializedName("line_name")
  private String lineName;

  @SerializedName("create_time")
  private long createTime;

  @SerializedName("point_count")
  private int pointCount;

  private ThemeLine(Builder builder) {
    setId(builder.id);
    setGameId(builder.gameId);
    setLineName(builder.lineName);
    setCreateTime(builder.createTime);
    setPointCount(builder.pointCount);
  }

  enum IS_DEL implements JsonDeserializer<IS_DEL>, JsonSerializer<IS_DEL> {
    FALSE(0),
    TRUE(1);

    private int value;

    IS_DEL(int value) {
      this.value = value;
    }

    @Override
    public IS_DEL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (IS_DEL state : IS_DEL.values()) {
        if (state.value == item) {
          return state;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for GAME_STATE is unexpected.",
                item));
      }
      return FALSE;
    }

    @Override
    public JsonElement serialize(IS_DEL src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.value);
    }
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }

  public String getLineName() {
    return lineName;
  }

  public void setLineName(String lineName) {
    this.lineName = lineName;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public int getPointCount() {
    return pointCount;
  }

  public void setPointCount(int pointCount) {
    this.pointCount = pointCount;
  }


  public static final class Builder {
    private long id;
    private long gameId;
    private String lineName;
    private long createTime;
    private int pointCount;

    public Builder() {
    }

    public Builder id(long val) {
      id = val;
      return this;
    }

    public Builder gameId(long val) {
      gameId = val;
      return this;
    }

    public Builder lineName(String val) {
      lineName = val;
      return this;
    }

    public Builder createTime(long val) {
      createTime = val;
      return this;
    }

    public Builder pointCount(int val) {
      pointCount = val;
      return this;
    }

    public ThemeLine build() {
      return new ThemeLine(this);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeLong(this.gameId);
    dest.writeString(this.lineName);
    dest.writeLong(this.createTime);
    dest.writeInt(this.pointCount);
  }

  protected ThemeLine(Parcel in) {
    this.id = in.readLong();
    this.gameId = in.readLong();
    this.lineName = in.readString();
    this.createTime = in.readLong();
    this.pointCount = in.readInt();
  }

  public static final Creator<ThemeLine> CREATOR = new Creator<ThemeLine>() {
    @Override
    public ThemeLine createFromParcel(Parcel source) {
      return new ThemeLine(source);
    }

    @Override
    public ThemeLine[] newArray(int size) {
      return new ThemeLine[size];
    }
  };
}
