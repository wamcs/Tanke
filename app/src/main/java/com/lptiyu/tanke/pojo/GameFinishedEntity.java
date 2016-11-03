package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.lptiyu.tanke.entity.BaseEntity;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/25
 *
 * @author ldx
 */
public class GameFinishedEntity extends BaseEntity implements Parcelable {
    @SerializedName("game_id")
    public long gameId;

    @SerializedName("img")
    public String img;

    @SerializedName("name")
    public String name;

    @SerializedName("start_time")
    public String startTime;

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    @SerializedName("time")
    public String totalTime;

    @SerializedName("end_time")
    public String endTime;

    public int type;

    @SerializedName("values")
    public int expPoints;

    @SerializedName("states")
    public int states;

    @SerializedName("is_del")
    public int is_del;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.gameId);
        dest.writeString(this.img);
        dest.writeString(this.name);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.totalTime);
        dest.writeInt(this.type);
        dest.writeInt(this.expPoints);
        dest.writeInt(this.states);
        dest.writeInt(this.is_del);
    }

    public GameFinishedEntity() {
    }

    protected GameFinishedEntity(Parcel in) {
        this.gameId = in.readLong();
        this.img = in.readString();
        this.name = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.totalTime = in.readString();
        this.type = in.readInt();
        this.expPoints = in.readInt();
        this.states = in.readInt();
        this.is_del = in.readInt();

    }

    public static final Parcelable.Creator<GameFinishedEntity> CREATOR = new Parcelable.Creator<GameFinishedEntity>() {
        @Override
        public GameFinishedEntity createFromParcel(Parcel source) {
            return new GameFinishedEntity(source);
        }

        @Override
        public GameFinishedEntity[] newArray(int size) {
            return new GameFinishedEntity[size];
        }
    };
}
