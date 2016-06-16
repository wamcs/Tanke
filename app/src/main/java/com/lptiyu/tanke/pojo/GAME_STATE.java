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
public enum GAME_STATE implements JsonSerializer<GAME_STATE>, JsonDeserializer<GAME_STATE> {
  NORMAL(0),
  ALPHA_TEST(1),
  MAINTAINING(2),
  FINISHED(3),
  WTF(4);

  public int value;

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

