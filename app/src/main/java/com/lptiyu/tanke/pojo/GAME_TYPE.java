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
