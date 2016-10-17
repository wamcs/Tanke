package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/10/14.
 */

public class StartRun implements Parcelable {

    public String exp;
    public int points;
    public int extra_points;
    public int extra_money;
    public String record_id;

    @Override
    public String toString() {
        return "StartRun{" +
                "exp='" + exp + '\'' +
                ", points=" + points +
                ", extra_points=" + extra_points +
                ", extra_money=" + extra_money +
                ", record_id='" + record_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.exp);
        dest.writeInt(this.points);
        dest.writeInt(this.extra_points);
        dest.writeInt(this.extra_money);
        dest.writeString(this.record_id);
    }

    public StartRun() {
    }

    protected StartRun(Parcel in) {
        this.exp = in.readString();
        this.points = in.readInt();
        this.extra_points = in.readInt();
        this.extra_money = in.readInt();
        this.record_id = in.readString();
    }

    public static final Parcelable.Creator<StartRun> CREATOR = new Parcelable.Creator<StartRun>() {
        @Override
        public StartRun createFromParcel(Parcel source) {
            return new StartRun(source);
        }

        @Override
        public StartRun[] newArray(int size) {
            return new StartRun[size];
        }
    };
}
