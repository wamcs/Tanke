package com.lptiyu.tanke.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public class User implements Parcelable {
  private int uid;
  private String token;

  @SerializedName("img")
  private String avatar;

  @SerializedName("name")
  private String nickname;

  private String phone;

  private String birthday;

  private String sex;

  @SerializedName("high")
  private String height;

  private String weight;

  private String address;

  @SerializedName("num")
  private int playing_game_num;

  @SerializedName("Finish_num")
  private int finished_game_num;

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

  public int getPlaying_game_num() {
    return playing_game_num;
  }

  public void setPlaying_game_num(int playing_game_num) {
    this.playing_game_num = playing_game_num;
  }

  public int getFinished_game_num() {
    return finished_game_num;
  }

  public void setFinished_game_num(int finished_game_num) {
    this.finished_game_num = finished_game_num;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
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

  @Override
  public String toString() {
    return "User{" +
        "uid=" + uid +
        ", token='" + token + '\'' +
        ", avatar='" + avatar + '\'' +
        ", nickname='" + nickname + '\'' +
        ", phone='" + phone + '\'' +
        ", birthday='" + birthday + '\'' +
        ", sex='" + sex + '\'' +
        ", height='" + height + '\'' +
        ", weight='" + weight + '\'' +
        ", address='" + address + '\'' +
        ", playing_game_num=" + playing_game_num +
        ", finished_game_num=" + finished_game_num +
        '}';
  }

  public User() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.uid);
    dest.writeString(this.token);
    dest.writeString(this.avatar);
    dest.writeString(this.nickname);
    dest.writeString(this.phone);
    dest.writeString(this.birthday);
    dest.writeString(this.sex);
    dest.writeString(this.height);
    dest.writeString(this.weight);
    dest.writeString(this.address);
    dest.writeInt(this.playing_game_num);
    dest.writeInt(this.finished_game_num);
  }

  protected User(Parcel in) {
    this.uid = in.readInt();
    this.token = in.readString();
    this.avatar = in.readString();
    this.nickname = in.readString();
    this.phone = in.readString();
    this.birthday = in.readString();
    this.sex = in.readString();
    this.height = in.readString();
    this.weight = in.readString();
    this.address = in.readString();
    this.playing_game_num = in.readInt();
    this.finished_game_num = in.readInt();
  }

  public static final Creator<User> CREATOR = new Creator<User>() {
    @Override
    public User createFromParcel(Parcel source) {
      return new User(source);
    }

    @Override
    public User[] newArray(int size) {
      return new User[size];
    }
  };
}
