package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/9/29.
 */

public class Recommend implements Parcelable {
    /**
     * id : 44
     * pic : http://test.lptiyu.com/run/Public/Upload/pic//game/2016-09-03/57ca376dc6a42.jpg
     * title : 正午一梦
     * area : 线上
     * city : 0
     * time_type : 0
     * start_date :
     * end_date :
     * start_time :
     * end_time :
     * state : 4
     * type : 2
     * recommend : 1
     * address_short :
     * difficulty : 0
     * play_status : 0
     */

    public String id;
    public String pic;
    public String title;
    public String area;
    public String city;
    public String time_type;
    public String start_date;
    public String end_date;
    public String start_time;
    public String end_time;
    public int state;
    public int type;
    public int recommend;
    public String address_short;
    public int difficulty;
    public String play_status;

    @Override
    public String toString() {
        return "Recommend{" +
                "id='" + id + '\'' +
                ", pic='" + pic + '\'' +
                ", title='" + title + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", time_type='" + time_type + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", state=" + state +
                ", type=" + type +
                ", recommend=" + recommend +
                ", address_short='" + address_short + '\'' +
                ", difficulty=" + difficulty +
                ", play_status='" + play_status + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.pic);
        dest.writeString(this.title);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.time_type);
        dest.writeString(this.start_date);
        dest.writeString(this.end_date);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeInt(this.state);
        dest.writeInt(this.type);
        dest.writeInt(this.recommend);
        dest.writeString(this.address_short);
        dest.writeInt(this.difficulty);
        dest.writeString(this.play_status);
    }

    public Recommend() {
    }

    protected Recommend(Parcel in) {
        this.id = in.readString();
        this.pic = in.readString();
        this.title = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.time_type = in.readString();
        this.start_date = in.readString();
        this.end_date = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.state = in.readInt();
        this.type = in.readInt();
        this.recommend = in.readInt();
        this.address_short = in.readString();
        this.difficulty = in.readInt();
        this.play_status = in.readString();
    }

    public static final Parcelable.Creator<Recommend> CREATOR = new Parcelable.Creator<Recommend>() {
        @Override
        public Recommend createFromParcel(Parcel source) {
            return new Recommend(source);
        }

        @Override
        public Recommend[] newArray(int size) {
            return new Recommend[size];
        }
    };
}
