package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.enums.PointTaskStatus;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/8/2.
 */
public class Point implements Comparable<Point>, Parcelable {
    /**
     * id : 1
     * line_id : 1
     * point_index : 1
     * latitude : 30.536156
     * longitude : 114.408642
     * address_name : 艺术与传媒学院
     * point_title : 尸体线索
     * point_img : point1.jpg
     * first_task : task1_1.json
     */

    public String id;
    public String line_id;
    public String point_index;
    public String latitude;
    public String longitude;
    public String address_name;
    public String point_title;
    public String point_img;
    public String first_task;
    public int state = PointTaskStatus.UNSTARTED;

    public boolean isNew = false;

    public ArrayList<Task> task_list;

    @Override
    public int compareTo(Point another) {
        if (another != null) {
            if (Integer.parseInt(this.point_index) > Integer.parseInt(another.point_index)) {
                return 1;
            } else if (Integer.parseInt(this.point_index) == Integer.parseInt(another.point_index)) {
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
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", line_id='" + line_id + '\'' +
                ", point_index='" + point_index + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", address_name='" + address_name + '\'' +
                ", point_title='" + point_title + '\'' +
                ", point_img='" + point_img + '\'' +
                ", first_task='" + first_task + '\'' +
                ", state=" + state +
                ", isNew=" + isNew +
                ", task_list=" + task_list +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.line_id);
        dest.writeString(this.point_index);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.address_name);
        dest.writeString(this.point_title);
        dest.writeString(this.point_img);
        dest.writeString(this.first_task);
        dest.writeInt(this.state);
        dest.writeByte(this.isNew ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.task_list);
    }

    public Point() {
    }

    protected Point(Parcel in) {
        this.id = in.readString();
        this.line_id = in.readString();
        this.point_index = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.address_name = in.readString();
        this.point_title = in.readString();
        this.point_img = in.readString();
        this.first_task = in.readString();
        this.state = in.readInt();
        this.isNew = in.readByte() != 0;
        this.task_list = in.createTypedArrayList(Task.CREATOR);
    }

    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel source) {
            return new Point(source);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}
