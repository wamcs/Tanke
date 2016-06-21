package com.lptiyu.tanke.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class GameManageEntity {
  long id;

  /**
   * Game的图片
   */
  @SerializedName("pic")
  String img;

  String title;

  /**
   * 任务的通关标准
   */
  String content;

  /**
   * 游戏的标题
   */
  @SerializedName("name")
  String gameTitle;

  /**
   * 二维码的内容，需要用此来生成二维码
   */
  @SerializedName("img")
  String qrcode;

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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getGameTitle() {
    return gameTitle;
  }

  public void setGameTitle(String gameTitle) {
    this.gameTitle = gameTitle;
  }

  public String getQrcode() {
    return qrcode;
  }

  public void setQrcode(String qrcode) {
    this.qrcode = qrcode;
  }
}
