package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class UserEntity {

  private long uid;

  private String token;

  @SerializedName("img")
  private String avatar;

  @SerializedName("name")
  private String nickname;

  private String phone;

  // 0:不是第一次登录 1：是第一次登录
  // 只在第三方登录时有用
  @SerializedName("type")
  private int isNewUserThirdParty = 1;

  public long getUid() {
    return uid;
  }

  public void setUid(long uid) {
    this.uid = uid;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public int getIsNewUserThirdParty() {
    return isNewUserThirdParty;
  }

  public void setIsNewUserThirdParty(int isNewUserThirdParty) {
    this.isNewUserThirdParty = isNewUserThirdParty;
  }

  @Override
  public String toString() {
    return "UserEntity{" +
        "uid=" + uid +
        ", token='" + token + '\'' +
        ", avatar='" + avatar + '\'' +
        ", nickname='" + nickname + '\'' +
        ", phone='" + phone + '\'' +
        ", isNewUserThirdParty=" + isNewUserThirdParty +
        '}';
  }
}
