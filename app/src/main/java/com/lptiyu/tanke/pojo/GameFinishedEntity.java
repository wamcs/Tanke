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
    public String img;
    public String name;
    @SerializedName("start_time")
    public String startTime;
    @SerializedName("time")
    public String totalTime;
    @SerializedName("end_time")
    public String endTime;
    public int type;
    @SerializedName("values")
    public int expPoints;
    public int states;
    public int is_del;
    public String tag;
    public int team_id;
    public int record_id;

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
        dest.writeInt(this.record_id);
        dest.writeInt(this.expPoints);
        dest.writeString(this.tag);
        dest.writeInt(this.team_id);
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
        this.record_id = in.readInt();
        this.type = in.readInt();
        this.expPoints = in.readInt();
        this.states = in.readInt();
        this.is_del = in.readInt();
        this.tag = in.readString();
        this.team_id = in.readInt();

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
