package com.lptiyu.tanke.pojo;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class City {

  private int id;
  private String name;
  private String province;
  private String latitude;
  private String longitude;

  public int getId() {
    return id;
  }

  public void setId(int id) {
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

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongtitude() {
    return longitude;
  }

  public void setLongtitude(String longtitude) {
    this.longitude = longtitude;
  }
}
