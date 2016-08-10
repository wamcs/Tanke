package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class GameDetailsEntity implements Parcelable {

  @SerializedName("id")
  private long gameId;

  @SerializedName("pic")
  private String img;

  @SerializedName("title")
  private String title;

  @SerializedName("area")
  private String area;

  @SerializedName("start_date")
  private String startDate;

  @SerializedName("end_date")
  private String endDate;

  @SerializedName("start_time")
  private String startTime;

  @SerializedName("end_time")
  private String endTime;

  @SerializedName("type")
  private GAME_TYPE type;

  @SerializedName("num")
  private int peoplePlaying;

  @SerializedName("content")
  private String gameIntro;

  @SerializedName("rule")
  private String rule;

  @SerializedName("zip_url")
  private String zipUrl;

  @SerializedName("url")
  private String shareUrl;

  private String latitude;

  @SerializedName("longtitude")
  private String longitude;

  @SerializedName("min")
  private int minNum;

  @SerializedName("max")
  private int maxNum;


  public int getPeoplePlaying() {
    return peoplePlaying;
  }

  public void setPeoplePlaying(int peoplePlaying) {
    this.peoplePlaying = peoplePlaying;
  }

  public String getGameIntro() {
    return gameIntro;
  }

  public void setGameIntro(String gameIntro) {
    this.gameIntro = gameIntro;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public String getZipUrl() {
    return zipUrl;
  }

  public void setZipUrl(String zipUrl) {
    this.zipUrl = zipUrl;
  }

  public String getShareUrl() {
    return shareUrl;
  }

  public void setShareUrl(String shareUrl) {
    this.shareUrl = shareUrl;
  }

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public GAME_TYPE getType() {
    return type;
  }

  public void setType(GAME_TYPE type) {
    this.type = type;
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

  public int getMinNum() {
    return minNum;
  }

  public void setMinNum(int minNum) {
    this.minNum = minNum;
  }

  public int getMaxNum() {
    return maxNum;
  }

  public void setMaxNum(int maxNum) {
    this.maxNum = maxNum;
  }

  @Override
  public String toString() {
    return "GameDetailsEntity{" +
        "gameId=" + gameId +
        ", img='" + img + '\'' +
        ", title='" + title + '\'' +
        ", area='" + area + '\'' +
        ", startDate='" + startDate + '\'' +
        ", endDate='" + endDate + '\'' +
        ", startTime='" + startTime + '\'' +
        ", endTime='" + endTime + '\'' +
        ", type=" + type +
        ", peoplePlaying=" + peoplePlaying +
        ", gameIntro='" + gameIntro + '\'' +
        ", rule='" + rule + '\'' +
        ", zipUrl='" + zipUrl + '\'' +
        ", shareUrl='" + shareUrl + '\'' +
        ", latitude='" + latitude + '\'' +
        ", longitude='" + longitude + '\'' +
        ", minNum=" + minNum +
        ", maxNum=" + maxNum +
        '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.gameId);
    dest.writeString(this.img);
    dest.writeString(this.title);
    dest.writeString(this.area);
    dest.writeString(this.startDate);
    dest.writeString(this.endDate);
    dest.writeString(this.startTime);
    dest.writeString(this.endTime);
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    dest.writeInt(this.peoplePlaying);
    dest.writeString(this.gameIntro);
    dest.writeString(this.rule);
    dest.writeString(this.zipUrl);
    dest.writeString(this.shareUrl);
    dest.writeString(this.latitude);
    dest.writeString(this.longitude);
    dest.writeInt(this.minNum);
    dest.writeInt(this.maxNum);
  }

  public GameDetailsEntity() {
  }

  protected GameDetailsEntity(Parcel in) {
    this.gameId = in.readLong();
    this.img = in.readString();
    this.title = in.readString();
    this.area = in.readString();
    this.startDate = in.readString();
    this.endDate = in.readString();
    this.startTime = in.readString();
    this.endTime = in.readString();
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : GAME_TYPE.values()[tmpType];
    this.peoplePlaying = in.readInt();
    this.gameIntro = in.readString();
    this.rule = in.readString();
    this.zipUrl = in.readString();
    this.shareUrl = in.readString();
    this.latitude = in.readString();
    this.longitude = in.readString();
    this.minNum = in.readInt();
    this.maxNum = in.readInt();
  }

  public static final Creator<GameDetailsEntity> CREATOR = new Creator<GameDetailsEntity>() {
    @Override
    public GameDetailsEntity createFromParcel(Parcel source) {
      return new GameDetailsEntity(source);
    }

    @Override
    public GameDetailsEntity[] newArray(int size) {
      return new GameDetailsEntity[size];
    }
  };
}
