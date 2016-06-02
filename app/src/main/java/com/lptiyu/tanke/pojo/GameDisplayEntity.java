package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayEntity {
  protected long id;

  @SerializedName("pic")
  protected String img = "";

  protected String title = "";

  protected String area = "";

  protected String city = "";

  @SerializedName("start_date")
  protected String startDate = "";

  @SerializedName("end_date")
  protected String endDate = "";

  @SerializedName("start_time")
  protected String startTime = "";

  @SerializedName("end_time")
  protected String endTime = "";

  protected GAME_STATE state = GAME_STATE.NORMAL;

  //TODO 不需要Recommended字段了，推荐将单独作为一个字段
  protected RECOMMENDED_TYPE recommend = RECOMMENDED_TYPE.NORMAL;

  protected GAME_TYPE type = GAME_TYPE.INDIVIDUALS;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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

  public GAME_STATE getState() {
    return state;
  }

  public void setState(GAME_STATE state) {
    this.state = state;
  }

  public RECOMMENDED_TYPE getRecommend() {
    return recommend;
  }

  public void setRecommend(RECOMMENDED_TYPE recommend) {
    this.recommend = recommend;
  }

  public GAME_TYPE getType() {
    return type;
  }

  public void setType(GAME_TYPE type) {
    this.type = type;
  }


}
