package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageEntity implements Parcelable {

    public long id;
    public String title;
    public String pic;
    public String content;
    public String url;
    public long create_time;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.pic);
        dest.writeString(this.content);
        dest.writeString(this.url);
        dest.writeLong(this.create_time);
    }

    public MessageEntity() {
    }

    protected MessageEntity(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.pic = in.readString();
        this.content = in.readString();
        this.url = in.readString();
        this.create_time = in.readLong();
    }

    public static final Parcelable.Creator<MessageEntity> CREATOR = new Parcelable.Creator<MessageEntity>() {
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
