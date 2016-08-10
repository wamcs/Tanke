package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/8/2.
 */
public class ThemeLine implements Parcelable {
    /**
     * id : 1
     * game_id : 1
     * line_name : 唯一一条路线
     * point_count : 7
     */

    public String id;
    public String game_id;
    public String line_name;
    public String point_count;

    @Override
    public String toString() {
        return "ThemeLine{" +
                "id='" + id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", line_name='" + line_name + '\'' +
                ", point_count='" + point_count + '\'' +
                '}';
    }

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
    }

    public ThemeLine() {
    }

    protected ThemeLine(Parcel in) {
        this.id = in.readString();
        this.game_id = in.readString();
        this.line_name = in.readString();
        this.point_count = in.readString();
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
