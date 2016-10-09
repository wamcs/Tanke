package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016/9/29.
 */

public class HomeBannerAndHot implements Parcelable {
    public List<Banner> banner_list;
    public List<Recommend> recommend_list;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.banner_list);
        dest.writeList(this.recommend_list);
    }

    public HomeBannerAndHot() {
    }

    protected HomeBannerAndHot(Parcel in) {
        this.banner_list = in.createTypedArrayList(Banner.CREATOR);
        this.recommend_list = new ArrayList<Recommend>();
        in.readList(this.recommend_list, Recommend.class.getClassLoader());
    }

    public static final Parcelable.Creator<HomeBannerAndHot> CREATOR = new Parcelable.Creator<HomeBannerAndHot>() {
        @Override
        public HomeBannerAndHot createFromParcel(Parcel source) {
            return new HomeBannerAndHot(source);
        }

        @Override
        public HomeBannerAndHot[] newArray(int size) {
            return new HomeBannerAndHot[size];
        }
    };

    @Override
    public String toString() {
        return "HomeBannerAndHot{" +
                "banner_list=" + banner_list +
                ", recommend_list=" + recommend_list +
                '}';
    }
}
