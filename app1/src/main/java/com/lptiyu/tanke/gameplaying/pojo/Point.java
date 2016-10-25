package com.lptiyu.tanke.gameplaying.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xiaoxiaoda
 *         date: 16-5-25
 *         email: wonderfulifeel@gmail.com
 */
public class Point implements Parcelable {


    private long id;

    @SerializedName("line_id")
    private long lineId;

    @SerializedName("point_index")
    private int pointIndex;

    private double latitude;

    private double longitude;

    @SerializedName("address_name")
    private String addressName;

    @SerializedName("point_title")
    private String pointTitle;

    @SerializedName("point_img")
    private String pointImg;

    @SerializedName("first_task")
    private List<String> taskId;

    private Map<String, Task> taskMap;

    private String introImage;

    private int state;

    private Point(Builder builder) {
        setState(builder.state);
        setId(builder.id);
        setLineId(builder.lineId);
        setPointIndex(builder.pointIndex);
        setLatitude(builder.latitude);
        setLongitude(builder.longitude);
        setTaskId(builder.taskId);
        setTaskMap(builder.taskMap);
        setIntroImage(builder.introImage);
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLineId() {
        return lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<String> getTaskId() {
        return taskId;
    }

    public void setTaskId(List<String> taskId) {
        this.taskId = taskId;
    }

    public Map<String, Task> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(Map<String, Task> taskMap) {
        this.taskMap = taskMap;
    }

    public String getIntroImage() {
        return introImage;
    }

    public void setIntroImage(String introImage) {
        this.introImage = introImage;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Point{" +
                "state=" + state +
                ", id=" + id +
                ", lineId=" + lineId +
                ", pointIndex=" + pointIndex +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", taskId=" + taskId +
                ", taskMap=" + taskMap +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeLong(this.id);
        dest.writeLong(this.lineId);
        dest.writeInt(this.pointIndex);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeStringList(this.taskId);
        dest.writeInt(this.taskMap.size());
        for (Map.Entry<String, Task> entry : this.taskMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeString(this.introImage);
    }

    public Point() {
    }

    protected Point(Parcel in) {
        this.state = in.readInt();
        this.id = in.readLong();
        this.lineId = in.readLong();
        this.pointIndex = in.readInt();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.taskId = in.createStringArrayList();
        int taskMapSize = in.readInt();
        this.taskMap = new HashMap<String, Task>(taskMapSize);
        for (int i = 0; i < taskMapSize; i++) {
            String key = in.readString();
            Task value = in.readParcelable(Task.class.getClassLoader());
            this.taskMap.put(key, value);
        }
        this.introImage = in.readString();
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel source) {
            return new Point(source);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public static final class Builder {
        private int state;
        private long id;
        private long lineId;
        private int pointIndex;
        private double latitude;
        private double longitude;
        private List<String> taskId;
        private Map<String, Task> taskMap;
        private String introImage;

        private Builder() {
        }

        public Builder state(int val) {
            state = val;
            return this;
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder lineId(long val) {
            lineId = val;
            return this;
        }

        public Builder pointIndex(int val) {
            pointIndex = val;
            return this;
        }

        public Builder latitude(double val) {
            latitude = val;
            return this;
        }

        public Builder longitude(double val) {
            longitude = val;
            return this;
        }

        public Builder taskId(List<String> val) {
            taskId = val;
            return this;
        }

        public Builder taskMap(Map<String, Task> val) {
            taskMap = val;
            return this;
        }

        public Builder introImage(String val) {
            introImage = val;
            return this;
        }

        public Point build() {
            return new Point(this);
        }
    }
}
