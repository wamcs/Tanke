package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.entity.BaseEntity;

/**
 * Created by Jason on 2016/8/11.
 */
public class Banner extends BaseEntity implements Parcelable {
    public String image;
    public int type;
    public String content;

    @Override
    public String toString() {
        return "Banner{" +
                "image='" + image + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeInt(this.type);
        dest.writeString(this.content);
    }

    public Banner() {
    }

    protected Banner(Parcel in) {
        this.image = in.readString();
        this.type = in.readInt();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Banner> CREATOR = new Parcelable.Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel source) {
            return new Banner(source);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };
}
