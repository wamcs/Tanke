package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.database.DBTaskRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016/7/20.
 */
public class PointRecord implements Parcelable {
    private Long id;
    private String statu;
    private List<DBTaskRecord> task;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.statu);
        dest.writeList(this.task);
    }

    public PointRecord() {
    }

    protected PointRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.statu = in.readString();
        this.task = new ArrayList<DBTaskRecord>();
        in.readList(this.task, DBTaskRecord.class.getClassLoader());
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public List<DBTaskRecord> getTask() {
        return task;
    }

    public void setTask(List<DBTaskRecord> task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "PointRecord{" +
                "id=" + id +
                ", statu='" + statu + '\'' +
                ", task=" + task +
                '}';
    }
}
