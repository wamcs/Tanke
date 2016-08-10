package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/7/20.
 */
public class PointRecord implements Parcelable {
    public Long id;
    public String statu;
    //    public String point_id;
    public ArrayList<TaskRecord> task;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.statu);
        //        dest.writeString(this.point_id);
        dest.writeTypedList(this.task);
    }

    public PointRecord() {
    }

    protected PointRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.statu = in.readString();
        //        this.point_id = in.readString();
        this.task = in.createTypedArrayList(TaskRecord.CREATOR);
    }

    public static final Parcelable.Creator<PointRecord> CREATOR = new Parcelable.Creator<PointRecord>() {
        @Override
        public PointRecord createFromParcel(Parcel source) {
            return new PointRecord(source);
        }

        @Override
        public PointRecord[] newArray(int size) {
            return new PointRecord[size];
        }
    };

    @Override
    public String toString() {
        return "PointRecord{" +
                "id=" + id +
                ", statu='" + statu + '\'' +
                //                ", point_id='" + point_id + '\'' +
                ", task=" + task +
                '}';
    }
}
