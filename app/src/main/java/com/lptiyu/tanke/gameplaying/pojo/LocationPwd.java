package com.lptiyu.tanke.gameplaying.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-30
 *         email: wonderfulifeel@gmail.com
 */
public class LocationPwd implements Parcelable{

  private double latitude;

  private double longitude;

  private double radius;

  private LocationPwd(Builder builder) {
    setLatitude(builder.latitude);
    setLongitude(builder.longitude);
    setRadius(builder.radius);
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public LatLng getLatLng() {
    return new LatLng(latitude, longitude);
  }
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
    dest.writeDouble(this.radius);
  }

  public LocationPwd() {
  }

  protected LocationPwd(Parcel in) {
    this.latitude = in.readDouble();
    this.longitude = in.readDouble();
    this.radius = in.readDouble();
  }

  public static final Creator<LocationPwd> CREATOR = new Creator<LocationPwd>() {
    @Override
    public LocationPwd createFromParcel(Parcel source) {
      return new LocationPwd(source);
    }

    @Override
    public LocationPwd[] newArray(int size) {
      return new LocationPwd[size];
    }
  };

  public static final class Builder {
    private double latitude;
    private double longitude;
    private double radius;

    public Builder() {
    }

    public Builder latitude(double val) {
      latitude = val;
      return this;
    }

    public Builder longitude(double val) {
      longitude = val;
      return this;
    }

    public Builder radius(double val) {
      radius = val;
      return this;
    }

    public LocationPwd build() {
      return new LocationPwd(this);
    }
  }
}
