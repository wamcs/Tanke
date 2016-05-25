package com.lptiyu.tanke.gameplaying.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */
public class Point implements Parcelable {

  private long id;

  @SerializedName("line_id")
  private long lineId;

  @SerializedName("point_index")
  private int pointIndex;

  private String latitude;

  private String longitude;

  private String message;

  @SerializedName("qrcode_info")
  private String qrCodeInfo;

  private Point(Builder builder) {
    setId(builder.id);
    setLineId(builder.lineId);
    setPointIndex(builder.pointIndex);
    setLatitude(builder.latitude);
    setLongitude(builder.longitude);
    setMessage(builder.message);
    setQrCodeInfo(builder.qrCodeInfo);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getLineId() {
    return lineId;
  }

  public void setLineId(long lineId) {
    this.lineId = lineId;
  }

  public int getPointIndex() {
    return pointIndex;
  }

  public void setPointIndex(int pointIndex) {
    this.pointIndex = pointIndex;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getQrCodeInfo() {
    return qrCodeInfo;
  }

  public void setQrCodeInfo(String qrCodeInfo) {
    this.qrCodeInfo = qrCodeInfo;
  }


  public static final class Builder {
    private long id;
    private long lineId;
    private int pointIndex;
    private String latitude;
    private String longitude;
    private String message;
    private String qrCodeInfo;

    public Builder() {
    }

    public Builder id(long val) {
      id = val;
      return this;
    }

    public Builder lineId(long val) {
      lineId = val;
      return this;
    }

    public Builder pointIndex(int val) {
      pointIndex = val;
      return this;
    }

    public Builder latitude(String val) {
      latitude = val;
      return this;
    }

    public Builder longitude(String val) {
      longitude = val;
      return this;
    }

    public Builder message(String val) {
      message = val;
      return this;
    }

    public Builder qrCodeInfo(String val) {
      qrCodeInfo = val;
      return this;
    }

    public Point build() {
      return new Point(this);
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeLong(this.lineId);
    dest.writeInt(this.pointIndex);
    dest.writeString(this.latitude);
    dest.writeString(this.longitude);
    dest.writeString(this.message);
    dest.writeString(this.qrCodeInfo);
  }

  protected Point(Parcel in) {
    this.id = in.readLong();
    this.lineId = in.readLong();
    this.pointIndex = in.readInt();
    this.latitude = in.readString();
    this.longitude = in.readString();
    this.message = in.readString();
    this.qrCodeInfo = in.readString();
  }

  public static final Creator<Point> CREATOR = new Creator<Point>() {
    @Override
    public Point createFromParcel(Parcel source) {
      return new Point(source);
    }

    @Override
    public Point[] newArray(int size) {
      return new Point[size];
    }
  };
}
