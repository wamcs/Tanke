package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/7/20.
 */
public class TaskRecord implements Parcelable {
    public Long id;
    public String ftime;
    public String taskId;
    public String exp;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.ftime);
        dest.writeString(this.taskId);
        dest.writeString(this.exp);
    }

    public TaskRecord() {
    }

    protected TaskRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.ftime = in.readString();
        this.taskId = in.readString();
        this.exp = in.readString();
    }

    public static final Parcelable.Creator<TaskRecord> CREATOR = new Parcelable.Creator<TaskRecord>() {
        @Override
        public TaskRecord createFromParcel(Parcel source) {
            return new TaskRecord(source);
        }

        @Override
        public TaskRecord[] newArray(int size) {
            return new TaskRecord[size];
        }
    };

    @Override
    public String toString() {
        return "TaskRecord{" +
                "id=" + id +
                ", ftime='" + ftime + '\'' +
                ", taskId='" + taskId + '\'' +
                ", exp='" + exp + '\'' +
                '}';
    }
}
