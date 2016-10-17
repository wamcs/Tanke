package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/8/2.
 */
public class Task implements Comparable<Task>, Parcelable {
    public String id;
    public String point_id;
    public String content;
    public String type;
    public String pwd;
    public String task_index;
    //    public String next_task;
    public String ftime = "";
    public String exp;

    @Override
    public int compareTo(Task another) {
        if (another != null) {
            if (Integer.parseInt(this.task_index) > Integer.parseInt(another.task_index)) {
                return 1;
            } else if (Integer.parseInt(this.task_index) == Integer.parseInt(another.task_index)) {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.point_id);
        dest.writeString(this.content);
        dest.writeString(this.type);
        dest.writeString(this.pwd);
        dest.writeString(this.task_index);
        //        dest.writeString(this.next_task);
        dest.writeString(this.ftime);
        dest.writeString(this.exp);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.id = in.readString();
        this.point_id = in.readString();
        this.content = in.readString();
        this.type = in.readString();
        this.pwd = in.readString();
        this.task_index = in.readString();
        //        this.next_task = in.readString();
        this.ftime = in.readString();
        this.exp = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", point_id='" + point_id + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", pwd='" + pwd + '\'' +
                ", task_index='" + task_index + '\'' +
                //                ", next_task='" + next_task + '\'' +
                ", ftime='" + ftime + '\'' +
                ", exp='" + exp + '\'' +
                '}';
    }
}
