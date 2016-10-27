package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.entity.BaseEntity;

/**
 * Created by Jason on 2016/9/23.
 */

public class HomeTabEntity extends BaseEntity implements Parcelable {
    public String area = "";
    public String city = "";
    public String end_date = "";
    public String end_time = "";
    public long id;
    public String pic = "";
    public int recommend;
    public String start_date = "";
    public String start_time = "";
    public int state;
    public int time_type;
    public int type;
    public String game_zip_url = "";
    public int difficulty;
    public int player_num;
    public String tag;
    public int distince;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.area);
        dest.writeInt(this.cid);
        dest.writeString(this.city);
        dest.writeString(this.end_date);
        dest.writeString(this.end_time);
        dest.writeLong(this.id);
        dest.writeString(this.pic);
        dest.writeInt(this.recommend);
        dest.writeString(this.start_date);
        dest.writeString(this.start_time);
        dest.writeInt(this.state);
        dest.writeInt(this.time_type);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.play_status);
        dest.writeString(this.game_zip_url);
        dest.writeInt(this.difficulty);
        dest.writeInt(this.player_num);
        dest.writeString(this.tag);
        dest.writeInt(this.distince);
    }

    public HomeTabEntity() {
    }

    protected HomeTabEntity(Parcel in) {
        this.area = in.readString();
        this.cid = in.readInt();
        this.city = in.readString();
        this.end_date = in.readString();
        this.end_time = in.readString();
        this.id = in.readLong();
        this.pic = in.readString();
        this.recommend = in.readInt();
        this.start_date = in.readString();
        this.start_time = in.readString();
        this.state = in.readInt();
        this.time_type = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.play_status = in.readInt();
        this.game_zip_url = in.readString();
        this.difficulty = in.readInt();
        this.player_num = in.readInt();
        this.tag = in.readString();
        this.distince = in.readInt();
    }

    public static final Parcelable.Creator<HomeTabEntity> CREATOR = new Parcelable.Creator<HomeTabEntity>() {
        @Override
        public HomeTabEntity createFromParcel(Parcel source) {
            return new HomeTabEntity(source);
        }

        @Override
        public HomeTabEntity[] newArray(int size) {
            return new HomeTabEntity[size];
        }
    };
}
