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
    protected long id;

    @SerializedName("pic")
    protected String img = "";

    protected String title = "";

    protected String area = "";

    protected String city = "";

    @SerializedName("start_date")
    protected String startDate = "";

    @SerializedName("end_date")
    protected String endDate = "";

    @SerializedName("start_time")
    protected String startTime = "";

    @SerializedName("end_time")
    protected String endTime = "";

    @SerializedName("state")
    protected GAME_STATE state = GAME_STATE.NORMAL;

    //TODO 不需要Recommended字段了，推荐将单独作为一个字段
    @SerializedName("recommend")
    protected RECOMMENDED_TYPE recommend = RECOMMENDED_TYPE.NORMAL;

    @SerializedName("type")
    protected GAME_TYPE type = GAME_TYPE.INDIVIDUALS;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public GAME_STATE getState() {
        return state;
    }

    public void setState(GAME_STATE state) {
        this.state = state;
    }

    public RECOMMENDED_TYPE getRecommend() {
        return recommend;
    }

    public void setRecommend(RECOMMENDED_TYPE recommend) {
        this.recommend = recommend;
    }

    public GAME_TYPE getType() {
        return type;
    }

    public void setType(GAME_TYPE type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "GameDisplayEntity {" +
                "\n id=" + id +
                ",\n img='" + img + '\'' +
                ",\n title='" + title + '\'' +
                ",\n area='" + area + '\'' +
                ",\n city='" + city + '\'' +
                ",\n startDate='" + startDate + '\'' +
                ",\n endDate='" + endDate + '\'' +
                ",\n startTime='" + startTime + '\'' +
                ",\n endTime='" + endTime + '\'' +
                ",\n state=" + state +
                ",\n recommend=" + recommend +
                ",\n type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.img);
        dest.writeString(this.title);
        dest.writeString(this.area);
        dest.writeString(this.city);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeInt(this.recommend == null ? -1 : this.recommend.ordinal());
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public GameDisplayEntity() {
    }

    protected GameDisplayEntity(Parcel in) {
        this.id = in.readLong();
        this.img = in.readString();
        this.title = in.readString();
        this.area = in.readString();
        this.city = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : GAME_STATE.values()[tmpState];
        int tmpRecommend = in.readInt();
        this.recommend = tmpRecommend == -1 ? null : RECOMMENDED_TYPE.values()[tmpRecommend];
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : GAME_TYPE.values()[tmpType];
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
}
