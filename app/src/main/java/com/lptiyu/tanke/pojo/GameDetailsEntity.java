package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class GameDetailsEntity {

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
        '}';
  }
}