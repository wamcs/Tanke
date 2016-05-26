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
  private String content;

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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

}
