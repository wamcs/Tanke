package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class City implements Parcelable{

  private String id;
  private String name;
  private String province;
  private double latitude;
  private double longitude;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongtitude() {
    return longitude;
  }

  public void setLongtitude(double longitude) {
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "City{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", province='" + province + '\'' +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        '}';
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.province);
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
  }

  public City() {
  }

  protected City(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.province = in.readString();
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
  }

  public static final Creator<City> CREATOR = new Creator<City>() {
    @Override
    public City createFromParcel(Parcel source) {
      return new City(source);
    }

    @Override
    public City[] newArray(int size) {
      return new City[size];
    }
  };
}
