package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class CityStruct {

  @SerializedName("level")
  private String mLevel;
  @SerializedName("initial")
  private String mInitial;
  @SerializedName("name")
  private String mName;
  @SerializedName("province")
  private String mProvince;
  @SerializedName("adcode")
  private String mAdcode;
  @SerializedName("cx")
  private String mCx;
  @SerializedName("cy")
  private String mCy;
  @SerializedName("code")
  private String mCode;
  @SerializedName("pinyin")
  private String mPinyin;

  public String getmLevel() {
    return mLevel;
  }

  public void setmLevel(String mLevel) {
    this.mLevel = mLevel;
  }

  public String getmInitial() {
    return mInitial;
  }

  public void setmInitial(String mInitial) {
    this.mInitial = mInitial;
  }

  public String getmName() {
    return mName;
  }

  public void setmName(String mName) {
    this.mName = mName;
  }

  public String getmProvince() {
    return mProvince;
  }

  public void setmProvince(String mProvince) {
    this.mProvince = mProvince;
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

  public String getmCode() {
    return mCode;
  }

  public void setmCode(String mCode) {
    this.mCode = mCode;
  }

  public String getmPinyin() {
    return mPinyin;
  }

  public void setmPinyin(String mPinyin) {
    this.mPinyin = mPinyin;
  }


  public CityStruct() {
  }

}
