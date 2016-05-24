package com.lptiyu.tanke.bean;

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
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameEntry {
  protected int id;

  @SerializedName("pic")
  protected String img;

  protected String title;

  protected String area;

  protected String city;

  @SerializedName("start_date")
  protected String startDate;

  @SerializedName("end_date")
  protected String endDate;

  @SerializedName("start_time")
  protected String startTime;

  @SerializedName("end_time")
  protected String endTime;

  protected GAME_STATE state = GAME_STATE.NORMAL;

  protected RECOMMENDED_TYPE recommend = RECOMMENDED_TYPE.NORMAL;

  protected GAME_TYPE type = GAME_TYPE.INDIVIDUALS;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public GAME_STATE getState() {
    return state;
  }

  public void setState(GAME_STATE state) {
    this.state = state;
  }

  public RECOMMENDED_TYPE getRecommend() {
    return recommend;
  }

  public void setRecommend(RECOMMENDED_TYPE recommend) {
    this.recommend = recommend;
  }

  public GAME_TYPE getType() {
    return type;
  }

  public void setType(GAME_TYPE type) {
    this.type = type;
  }

  public enum GAME_STATE implements JsonSerializer<GAME_STATE>, JsonDeserializer<GAME_STATE> {
    NORMAL(0),
    ALPHA_TEST(1),
    MAINTAINING(2),
    FINISHED(3);

    public final int value;

    GAME_STATE(int value) {
      this.value = value;
    }

    @Override
    public JsonElement serialize(GAME_STATE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.value);
    }

    @Override
    public GAME_STATE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (GAME_STATE state : GAME_STATE.values()) {
        if (state.value == item) {
          return state;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for GAME_STATE is unexpected.",
                item));
      }
      return NORMAL;
    }


  }


  public enum RECOMMENDED_TYPE implements JsonSerializer<RECOMMENDED_TYPE>,
      JsonDeserializer<RECOMMENDED_TYPE> {
    NORMAL(0),
    RECOMMENDED(1);


    public final int value;

    RECOMMENDED_TYPE(int value) {
      this.value = value;
    }

    @Override
    public RECOMMENDED_TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (RECOMMENDED_TYPE recommended : RECOMMENDED_TYPE.values()) {
        if (recommended.value == item) {
          return recommended;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for GAME_STATE is unexpected.",
                item));
      }
      return NORMAL;
    }

    @Override
    public JsonElement serialize(RECOMMENDED_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.value);
    }
  }

  public enum GAME_TYPE implements JsonSerializer<GAME_TYPE>,
      JsonDeserializer<GAME_TYPE> {
    INDIVIDUALS(0),
    TEAMS(1);

    public final int value;

    GAME_TYPE(int value) {
      this.value = value;
    }

    @Override
    public JsonElement serialize(GAME_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.value);
    }

    @Override
    public GAME_TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (GAME_TYPE game_type : GAME_TYPE.values()) {
        if (game_type.value == item) {
          return game_type;
        }
      }

      if (BuildConfig.DEBUG) {
        throw new IllegalStateException(
            String.format("The item (%d) for GAME_TYPE is unexpected.",
                item));
      }
      return INDIVIDUALS;
    }

  }


}
