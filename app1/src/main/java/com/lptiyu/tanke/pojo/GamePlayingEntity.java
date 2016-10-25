package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/24
 *
 * @author ldx
 */
public class GamePlayingEntity implements Parcelable {
    @SerializedName("game_id")
    private long gameId;

    @SerializedName("img")
    private String img;

    @SerializedName("name")
    private String name;

    // 0: 上次玩的
    // 1: 正在进行的
    //TODO 上次玩的和正在进行的，需不需要区分
    @SerializedName("type")
    private int type;

    @SerializedName("num")
    private int num;

    @SerializedName("set")
    private float progress;


    @SerializedName("states")
    private int states;

    @SerializedName("is_del")
    private int is_del;

    public long getIsdel() {
        return is_del;
    }

    public void setIsdel(int is_del) {
        this.is_del = is_del;
    }

    public long getStates() {
        return states;
    }

    public void setStates(int states) {
        this.states = states;
    }


    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

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
