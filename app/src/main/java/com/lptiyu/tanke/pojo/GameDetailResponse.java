package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/8/5.
 */
public class GameDetailResponse implements Parcelable {
    /**
     * pic : http://api.lptiyu.com/run/Public/Upload/pic//game/2016-08-01/579eadfa14d38.png
     * title : 信
     * area : 西班牙风情街
     * city : 1
     * time_type : 0
     * start_date :
     * end_date :
     * start_time :
     * end_time :
     * type : 2
     * num : 112
     * content : <html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"/></head><body><p>无</p>
     * </body></html>
     * rule : null
     * longtitude : 114.412957
     * latitude : 30.508005
     * min : 0
     * max : 0
     * url : http://api.lptiyu.com/lepao/api.php/Home/game?game_id=3
     */

    public String pic;
    public String title;
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
    }

    public GameDetailResponse() {
    }

    protected GameDetailResponse(Parcel in) {
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
    }

    public static final Parcelable.Creator<GameDetailResponse> CREATOR = new Parcelable.Creator<GameDetailResponse>() {
        @Override
        public GameDetailResponse createFromParcel(Parcel source) {
            return new GameDetailResponse(source);
        }

        @Override
        public GameDetailResponse[] newArray(int size) {
            return new GameDetailResponse[size];
        }
    };

    @Override
    public String toString() {
        return "GameDetailResponse{" +
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
                ", rule=" + rule +
                ", longtitude='" + longtitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", url='" + url + '\'' +
                ", states='" + states + '\'' +
                '}';
    }
}
