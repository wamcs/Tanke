package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/18
 *
 * @author ldx
 */
public class GameDisplayEntity implements Parcelable {
    protected String area = "";

    protected String city = "";

    @SerializedName("end_date")
    protected String endDate = "";

    @SerializedName("end_time")
    protected String endTime = "";

    protected long id;

    @SerializedName("pic")
    protected String img = "";

    @SerializedName("recommend")
    protected int recommend;

    @SerializedName("start_date")
    protected String startDate = "";

    @SerializedName("start_time")
    protected String startTime = "";

    @SerializedName("state")
    protected int state;

    protected int time_type;

    protected String title = "";

    @SerializedName("type")
    protected int type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.endDate);
        dest.writeString(this.endTime);
        dest.writeLong(this.id);
        dest.writeString(this.img);
        dest.writeInt(this.recommend);
        dest.writeString(this.startDate);
        dest.writeString(this.startTime);
        dest.writeInt(this.state);
        dest.writeInt(this.time_type);
        dest.writeString(this.title);
        dest.writeInt(this.type);
    }

    public GameDisplayEntity() {
    }

    protected GameDisplayEntity(Parcel in) {
        this.area = in.readString();
        this.city = in.readString();
        this.endDate = in.readString();
        this.endTime = in.readString();
        this.id = in.readLong();
        this.img = in.readString();
        this.recommend = in.readInt();
        this.startDate = in.readString();
        this.startTime = in.readString();
        this.state = in.readInt();
        this.time_type = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<GameDisplayEntity> CREATOR = new Parcelable.Creator<GameDisplayEntity>() {
        @Override
        public GameDisplayEntity createFromParcel(Parcel source) {
            return new GameDisplayEntity(source);
        }

        @Override
        public GameDisplayEntity[] newArray(int size) {
            return new GameDisplayEntity[size];
        }
    };

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTime_type() {
        return time_type;
    }

    public void setTime_type(int time_type) {
        this.time_type = time_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
