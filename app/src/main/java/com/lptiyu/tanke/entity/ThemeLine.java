package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/8/2.
 */
public class ThemeLine implements Parcelable {

    public String id;
    public String game_id;
    public String line_name;
    public String point_count;
    public ArrayList<Point> point_list;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.game_id);
        dest.writeString(this.line_name);
        dest.writeString(this.point_count);
        dest.writeTypedList(this.point_list);
    }

    public ThemeLine() {
    }

    protected ThemeLine(Parcel in) {
        this.id = in.readString();
        this.game_id = in.readString();
        this.line_name = in.readString();
        this.point_count = in.readString();
        this.point_list = in.createTypedArrayList(Point.CREATOR);
    }

    public static final Parcelable.Creator<ThemeLine> CREATOR = new Parcelable.Creator<ThemeLine>() {
        @Override
        public ThemeLine createFromParcel(Parcel source) {
            return new ThemeLine(source);
        }

        @Override
        public ThemeLine[] newArray(int size) {
            return new ThemeLine[size];
        }
    };
}
