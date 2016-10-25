package com.lptiyu.tanke.location;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author: xiaoxiaoda
 * date: 15-12-2
 * email: daque@hustunique.com
 */
public class LocationStruct implements Parcelable {

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


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mProvinceName);
    dest.writeString(this.mAdcode);
    dest.writeString(this.mCx);
    dest.writeString(this.mCy);
    dest.writeString(this.mLevel);
    dest.writeTypedList(mCitys);
  }

  public LocationStruct() {
  }

  protected LocationStruct(Parcel in) {
    this.mProvinceName = in.readString();
    this.mAdcode = in.readString();
    this.mCx = in.readString();
    this.mCy = in.readString();
    this.mLevel = in.readString();
    this.mCitys = in.createTypedArrayList(CityStruct.CREATOR);
  }

  public static final Creator<LocationStruct> CREATOR = new Creator<LocationStruct>() {
    public LocationStruct createFromParcel(Parcel source) {
      return new LocationStruct(source);
    }

    public LocationStruct[] newArray(int size) {
      return new LocationStruct[size];
    }
  };
}
