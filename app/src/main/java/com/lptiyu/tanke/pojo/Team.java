package com.lptiyu.tanke.pojo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.List;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class Team {

  // 当前用户的身份
  @SerializedName("status")
  private UserStatus state;

  // 团队名称
  private String name;

  // 队员人数
  private int num;

  @SerializedName("low")
  // 游戏要求最低人数
  private int low;

  @SerializedName("high")
  // 游戏要求最高人数
  private int high;

  @SerializedName("data")
  private List<TeamMember> members;

  public UserStatus getState() {
    return state;
  }

  public void setState(UserStatus state) {
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public int getLow() {
    return low;
  }

  public void setLow(int low) {
    this.low = low;
  }

  public int getHigh() {
    return high;
  }

  public void setHigh(int high) {
    this.high = high;
  }

  public List<TeamMember> getMembers() {
    return members;
  }

  public void setMembers(List<TeamMember> members) {
    this.members = members;
  }

  public class TeamMember {
    @SerializedName("id")
    private int teamMemberId;

    // 用户的uid
    @SerializedName("user_id")
    private int uid;

    @SerializedName("img")
    private String avatar;

    @SerializedName("name")
    private String name;

    // 等级
    @SerializedName("num")
    private String grade;

    @SerializedName("type")
    private UserStatus state;

    public int getTeamMemberId() {
      return teamMemberId;
    }

    public void setTeamMemberId(int teamMemberId) {
      this.teamMemberId = teamMemberId;
    }

    public int getUid() {
      return uid;
    }

    public void setUid(int uid) {
      this.uid = uid;
    }

    public String getAvatar() {
      return avatar;
    }

    public void setAvatar(String avatar) {
      this.avatar = avatar;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getGrade() {
      return grade;
    }

    public void setGrade(String grade) {
      this.grade = grade;
    }

    public UserStatus getState() {
      return state;
    }

    public void setState(UserStatus state) {
      this.state = state;
    }
  }

  /**
   * 当前用户在团队身份, 是否是队长
   */
  public enum UserStatus implements JsonSerializer<UserStatus>,
      JsonDeserializer<UserStatus> {
    NORMAL(0),
    MASTER(1);

    public final int value;

    UserStatus(int value) {
      this.value = value;
    }

    @Override
    public JsonElement serialize(UserStatus src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.value);
    }

    @Override
    public UserStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      final int item = json.getAsInt();
      for (UserStatus status : UserStatus.values()) {
        if (status.value == item) {
          return status;
        }
      }
      return NORMAL;
    }


  }
}
