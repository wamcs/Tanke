package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/10/21.
 */

public class Jingwei implements Parcelable {
    public String jingwei;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.jingwei);
    }

    public Jingwei() {
    }

    protected Jingwei(Parcel in) {
        this.jingwei = in.readString();
    }

    public static final Parcelable.Creator<Jingwei> CREATOR = new Parcelable.Creator<Jingwei>() {
        @Override
        public Jingwei createFromParcel(Parcel source) {
            return new Jingwei(source);
        }

        @Override
        public Jingwei[] newArray(int size) {
            return new Jingwei[size];
        }
    };
}
