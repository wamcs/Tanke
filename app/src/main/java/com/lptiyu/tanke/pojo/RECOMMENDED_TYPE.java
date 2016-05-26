package com.lptiyu.tanke.pojo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lptiyu.tanke.BuildConfig;

import java.lang.reflect.Type;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/26
 *
 * @author ldx
 */

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

