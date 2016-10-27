package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.entity.BaseEntity;

import java.util.List;

/**
 * Created by Jason on 2016/8/5.
 */
public class GameDetail extends BaseEntity implements Parcelable {

    public String pic;
    //    public String title;
    public String area;
    public String city;
    public String time_type;
    public String start_date;
    public String end_date;
    public String start_time;
    public String end_time;
    public int type;
    public String num;
    public String content;
    public String rule;
    public String longtitude;
    public String latitude;
    public String min;
    public String max;
    public String url;
    public int states;
    public int difficulty;
    //    public int cid;
    public List<Ranks> rank_list;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pic);
        dest.writeString(this.title);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.time_type);
        dest.writeString(this.start_date);
        dest.writeString(this.end_date);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeInt(this.type);
        dest.writeString(this.num);
        dest.writeString(this.content);
        dest.writeString(this.rule);
        dest.writeString(this.longtitude);
        dest.writeString(this.latitude);
        dest.writeString(this.min);
        dest.writeString(this.max);
        dest.writeString(this.url);
        dest.writeInt(this.states);
        dest.writeInt(this.difficulty);
        dest.writeInt(this.cid);
        dest.writeTypedList(this.rank_list);
    }

    public GameDetail() {
    }

    protected GameDetail(Parcel in) {
        this.pic = in.readString();
        this.title = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.time_type = in.readString();
        this.start_date = in.readString();
        this.end_date = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.type = in.readInt();
        this.num = in.readString();
        this.content = in.readString();
        this.rule = in.readString();
        this.longtitude = in.readString();
        this.latitude = in.readString();
        this.min = in.readString();
        this.max = in.readString();
        this.url = in.readString();
        this.states = in.readInt();
        this.difficulty = in.readInt();
        this.cid = in.readInt();
        this.rank_list = in.createTypedArrayList(Ranks.CREATOR);
    }

    public static final Parcelable.Creator<GameDetail> CREATOR = new Parcelable.Creator<GameDetail>() {
        @Override
        public GameDetail createFromParcel(Parcel source) {
            return new GameDetail(source);
        }

        @Override
        public GameDetail[] newArray(int size) {
            return new GameDetail[size];
        }
    };

    @Override
    public String toString() {
        return "GameDetail{" +
                "pic='" + pic + '\'' +
                ", title='" + title + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", time_type='" + time_type + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", type=" + type +
                ", num='" + num + '\'' +
                ", content='" + content + '\'' +
                ", rule='" + rule + '\'' +
                ", longtitude='" + longtitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", url='" + url + '\'' +
                ", states=" + states +
                ", difficulty=" + difficulty +
                ", cid=" + cid +
                ", rank_list=" + rank_list +
                '}';
    }
}
