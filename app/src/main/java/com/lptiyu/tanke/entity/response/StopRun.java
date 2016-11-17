package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/10/14.
 */

public class StopRun implements Parcelable {

    public String uid;
    public String game_id;
    public int time;
    public String distance;
    public String exp;
    public int points;
    public String extra_money;
    public String pass_tit;
    public String pass_intro;
    public String pass_tips;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.game_id);
        dest.writeInt(this.time);
        dest.writeString(this.distance);
        dest.writeString(this.exp);
        dest.writeInt(this.points);
        dest.writeString(this.extra_money);
        dest.writeString(this.pass_tit);
        dest.writeString(this.pass_intro);
        dest.writeString(this.pass_tips);
    }

    public StopRun() {
    }

    protected StopRun(Parcel in) {
        this.uid = in.readString();
        this.game_id = in.readString();
        this.time = in.readInt();
        this.distance = in.readString();
        this.exp = in.readString();
        this.points = in.readInt();
        this.extra_money = in.readString();
        this.pass_tit = in.readString();
        this.pass_intro = in.readString();
        this.pass_tips = in.readString();
    }

    public static final Parcelable.Creator<StopRun> CREATOR = new Parcelable.Creator<StopRun>() {
        @Override
        public StopRun createFromParcel(Parcel source) {
            return new StopRun(source);
        }

        @Override
        public StopRun[] newArray(int size) {
            return new StopRun[size];
        }
    };
}
