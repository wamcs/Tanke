package com.lptiyu.tanke.global;


import com.lptiyu.tanke.utils.ShaPrefer;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 15/12/5
 *
 * @author ldx
 */
public class Accounts {

  public static void setPhoneNumber(String number) {
    ShaPrefer.put("user_phone", number);
  }

  public static String getPhoneNumber() {
    return ShaPrefer.getString("user_phone", "");
  }

  public static void setSecret(String secret){
    ShaPrefer.put("user_secret",secret);
  }

  public static String getSecret(){
    return ShaPrefer.getString("user_secret","");
  }

  public static String getKey() {
    return ShaPrefer.getString("user_key", "");
  }

  public static void setKey(String s) {
    ShaPrefer.put("user_key", s);
  }

  public static String getOpenId() {
    return ShaPrefer.getString("user_openId", "");
  }

  public static void setOpenId(String openId) {
    ShaPrefer.put("user_openId", openId);
  }

  public static boolean isLogin() {
    String key = getKey();
    return (key != null) && (!key.isEmpty());
  }

  public static void logOut() {
    setKey("");
  }
}
