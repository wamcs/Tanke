package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public class UserDetails {

  @SerializedName("name")
  private String nickname;

  @SerializedName("img")
  private String avatar;

  @SerializedName("phone")
  private String phone;

  private String birthday;

  private String sex;

  @SerializedName("high")
  private String height;

  private String weight;

  private String address;

  @SerializedName("num")
  private int playingGameNum;

  @SerializedName("Finish_num")
  private int finishedGameNum;

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getPlayingGameNum() {
    return playingGameNum;
  }

  public void setPlayingGameNum(int playingGameNum) {
    this.playingGameNum = playingGameNum;
  }

  public int getFinishedGameNum() {
    return finishedGameNum;
  }

  public void setFinishedGameNum(int finishedGameNum) {
    this.finishedGameNum = finishedGameNum;
  }

  @Override
  public String toString() {
    return "UserDetails{" +
        "nickname='" + nickname + '\'' +
        ", avatar='" + avatar + '\'' +
        ", phone='" + phone + '\'' +
        ", birthday='" + birthday + '\'' +
        ", sex='" + sex + '\'' +
        ", height='" + height + '\'' +
        ", weight='" + weight + '\'' +
        ", address='" + address + '\'' +
        ", playingGameNum=" + playingGameNum +
        ", finishedGameNum=" + finishedGameNum +
        '}';
  }
}
