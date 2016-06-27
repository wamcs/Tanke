package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author : xiaoxiaoda
 *         date: 16-6-20
 *         email: wonderfulifeel@gmail.com
 */

/**
 * This pojo is match with lepao server
 */
public class MessageEntity implements Parcelable {

  private long id;

  private String title;

  @SerializedName("pic")
  private String imgUrl;

  private String content;

  private String url;

  @SerializedName("create_time")
  private long createTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeString(this.title);
    dest.writeString(this.imgUrl);
    dest.writeString(this.content);
    dest.writeString(this.url);
    dest.writeLong(this.createTime);
  }

  public MessageEntity() {
  }

  protected MessageEntity(Parcel in) {
    this.id = in.readLong();
    this.title = in.readString();
    this.imgUrl = in.readString();
    this.content = in.readString();
    this.url = in.readString();
    this.createTime = in.readLong();
  }

  public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
    @Override
    public MessageEntity createFromParcel(Parcel source) {
      return new MessageEntity(source);
    }

    @Override
    public MessageEntity[] newArray(int size) {
      return new MessageEntity[size];
    }
  };
}
