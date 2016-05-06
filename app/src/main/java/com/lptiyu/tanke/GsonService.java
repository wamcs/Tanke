package com.lptiyu.tanke;

import com.google.gson.Gson;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-5
 *         email: wonderfulifeel@gmail.com
 */
public class GsonService {

  public static <T> T parseJson(String jsonString, Class<T> clazz) {
    T t = null;
    try {
      Gson gson = new Gson();
      t = gson.fromJson(jsonString, clazz);
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      System.out.println("解析json失败");
    }
    return t;

  }
}
