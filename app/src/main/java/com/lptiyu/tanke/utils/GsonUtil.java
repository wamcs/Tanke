package com.lptiyu.tanke.utils;

import com.google.gson.Gson;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-17
 *         email: wonderfulifeel@gmail.com
 */
public class GsonUtil {

  public static Gson sGson = new Gson();

  public static <T> T parseJson(String jsonString, Class<T> clazz) {
    T t = null;
    try {
      Gson gson = sGson;
      t = gson.fromJson(jsonString, clazz);
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      System.out.println("解析json失败");
    }
    return t;
  }
}
