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

}
