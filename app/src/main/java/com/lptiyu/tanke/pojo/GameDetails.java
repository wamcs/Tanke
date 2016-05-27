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
public class GameDetails extends GameDisplayEntity implements Parcelable {

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


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.peoplePlaying);
    dest.writeString(this.content);
    dest.writeString(this.rule);
    dest.writeString(this.zipUrl);
    dest.writeString(this.shareUrl);
    dest.writeLong(this.id);
    dest.writeString(this.img);
    dest.writeString(this.title);
    dest.writeString(this.area);
    dest.writeString(this.city);
    dest.writeString(this.startDate);
    dest.writeString(this.endDate);
    dest.writeString(this.startTime);
    dest.writeString(this.endTime);
    dest.writeInt(this.state == null ? -1 : this.state.ordinal());
    dest.writeInt(this.recommend == null ? -1 : this.recommend.ordinal());
    dest.writeInt(this.type == null ? -1 : this.type.ordinal());
  }

  public GameDetails() {
  }

  protected GameDetails(Parcel in) {
    this.peoplePlaying = in.readInt();
    this.content = in.readString();
    this.rule = in.readString();
    this.zipUrl = in.readString();
    this.shareUrl = in.readString();
    this.id = in.readInt();
    this.img = in.readString();
    this.title = in.readString();
    this.area = in.readString();
    this.city = in.readString();
    this.startDate = in.readString();
    this.endDate = in.readString();
    this.startTime = in.readString();
    this.endTime = in.readString();
    int tmpState = in.readInt();
    this.state = tmpState == -1 ? null : GAME_STATE.values()[tmpState];
    int tmpRecommend = in.readInt();
    this.recommend = tmpRecommend == -1 ? null : RECOMMENDED_TYPE.values()[tmpRecommend];
    int tmpType = in.readInt();
    this.type = tmpType == -1 ? null : GAME_TYPE.values()[tmpType];
  }

  public static final Parcelable.Creator<GameDetails> CREATOR = new Parcelable.Creator<GameDetails>() {
    @Override
    public GameDetails createFromParcel(Parcel source) {
      return new GameDetails(source);
    }

    @Override
    public GameDetails[] newArray(int size) {
      return new GameDetails[size];
    }
  };
}
