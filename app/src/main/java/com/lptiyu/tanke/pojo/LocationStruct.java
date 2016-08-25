package com.lptiyu.tanke.pojo;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationStruct {

  @SerializedName("name")
  private String mProvinceName;
  @SerializedName("adcode")
  private String mAdcode;
  @SerializedName("cx")
  private String mCx;
  @SerializedName("cy")
  private String mCy;
  @SerializedName("level")
  private String mLevel;
  @SerializedName("cities")
  private ArrayList<CityStruct> mCitys;

  public String getmProvinceName() {
    return mProvinceName;
  }

  public void setmProvinceName(String mProvinceName) {
    this.mProvinceName = mProvinceName;
  }

  public String getmAdcode() {
    return mAdcode;
  }

  public void setmAdcode(String mAdcode) {
    this.mAdcode = mAdcode;
  }

  public String getmCx() {
    return mCx;
  }

  public void setmCx(String mCx) {
    this.mCx = mCx;
  }

  public String getmCy() {
    return mCy;
  }

  public void setmCy(String mCy) {
    this.mCy = mCy;
  }

  public String getmLevel() {
    return mLevel;
  }

  public void setmLevel(String mLevel) {
    this.mLevel = mLevel;
  }

  public ArrayList<CityStruct> getmCitys() {
    return mCitys;
  }

  public void setmCitys(ArrayList<CityStruct> mCitys) {
    this.mCitys = mCitys;
  }



  public LocationStruct() {
  }

}
