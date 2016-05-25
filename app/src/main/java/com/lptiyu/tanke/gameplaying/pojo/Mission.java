package com.lptiyu.tanke.gameplaying.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */
public class Mission {

  private long id;

  @SerializedName("point_id")
  private long pointId;

  @SerializedName("type")
  private MISSION_TYPE type;

  private int exp;

  @SerializedName("mission_name")
  private String missionName;

  @SerializedName("image")
  private String imageUrl;

  private String content;

  private String pwd;

  public enum MISSION_TYPE {
    SCAN_CODE(0), // scan the QRCode
    LOCATE(1), // locate the position
    RIDDLE(2), // Secret mission
    DISTINGUISH(3), // take the photo and distinguish
    TIMING(4),
    FINISH(5);

    private int type;

    MISSION_TYPE(int type) {
      this.type = type;
    }

    public int getType() {
      return type;
    }

  }
}
