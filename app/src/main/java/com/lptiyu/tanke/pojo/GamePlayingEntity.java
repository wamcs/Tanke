package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.lptiyu.tanke.entity.BaseEntity;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class GamePlayingEntity extends BaseEntity implements Parcelable {
    @SerializedName("game_id")
    public long gameId;

    @SerializedName("img")
    public String img;

    @SerializedName("name")
    public String name;

    // 0: 上次玩的
    // 1: 正在进行的
    //TODO 上次玩的和正在进行的，需不需要区分
    @SerializedName("type")
    public int type;

    @SerializedName("num")
    public int num;

    @SerializedName("set")
    public float progress;

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
        dest.writeInt(this.type);
        dest.writeInt(this.num);
        dest.writeFloat(this.progress);
        dest.writeInt(this.states);
        dest.writeInt(this.is_del);
    }

    public GamePlayingEntity() {
    }

    protected GamePlayingEntity(Parcel in) {
        this.gameId = in.readLong();
        this.img = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.num = in.readInt();
        this.progress = in.readFloat();
        this.states = in.readInt();
        this.is_del = in.readInt();
    }

    public static final Parcelable.Creator<GamePlayingEntity> CREATOR = new Parcelable.Creator<GamePlayingEntity>() {
        @Override
        public GamePlayingEntity createFromParcel(Parcel source) {
            return new GamePlayingEntity(source);
        }

        @Override
        public GamePlayingEntity[] newArray(int size) {
            return new GamePlayingEntity[size];
        }
    };

    @Override
    public String toString() {
        return "GamePlayingEntity{" +
                "gameId=" + gameId +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", num=" + num +
                ", progress=" + progress +
                ", states=" + states +
                ", is_del=" + is_del +
                '}';
    }
}
