package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public class UserDetails implements Parcelable {

    @SerializedName("name")
    private String nickname;

    @SerializedName("img")
    private String avatar;

    @SerializedName("phone")
    private String phone;

    private String birthday;

    private String sex;

    @SerializedName("high")
    private String height;

    private String weight;

    private String address;

    @SerializedName("num")
    private int playingGameNum;

    @SerializedName("finish_num")
    private int finishedGameNum;

    @SerializedName("experience")
    private int exp;

    @SerializedName("experiencelast")
    private int next_exp;

    @SerializedName("level")
    private int level;


    @SerializedName("task_count")
    private int task_count;

    private String money;
    private String points;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPlayingGameNum() {
        return playingGameNum;
    }

    public void setPlayingGameNum(int playingGameNum) {
        this.playingGameNum = playingGameNum;
    }

    public int getFinishedGameNum() {
        return finishedGameNum;
    }

    public void setFinishedGameNum(int finishedGameNum) {
        this.finishedGameNum = finishedGameNum;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getNextExp() {
        return next_exp;
    }

    public void setNextExp(int exp) {
        this.next_exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTaskCount() {
        return task_count;
    }

    public void setTaskCount(int taskCount) {
        this.task_count = taskCount;
    }


    @Override
    public String toString() {
        return "UserDetails{" +
                "nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phone='" + phone + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", address='" + address + '\'' +
                ", playingGameNum=" + playingGameNum +
                ", finishedGameNum=" + finishedGameNum +
                ", exp=" + exp +
                ", next_exp=" + next_exp +
                ", level=" + level +
                ", task_count=" + task_count +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickname);
        dest.writeString(this.avatar);
        dest.writeString(this.phone);
        dest.writeString(this.birthday);
        dest.writeString(this.sex);
        dest.writeString(this.height);
        dest.writeString(this.weight);
        dest.writeString(this.address);
        dest.writeString(this.money);
        dest.writeString(this.points);
        dest.writeInt(this.playingGameNum);
        dest.writeInt(this.finishedGameNum);
        dest.writeInt(this.exp);
        dest.writeInt(this.next_exp);
        dest.writeInt(this.level);
        dest.writeInt(this.task_count);

    }

    public UserDetails() {
    }

    protected UserDetails(Parcel in) {
        this.nickname = in.readString();
        this.avatar = in.readString();
        this.phone = in.readString();
        this.birthday = in.readString();
        this.sex = in.readString();
        this.height = in.readString();
        this.points = in.readString();
        this.money = in.readString();
        this.weight = in.readString();
        this.address = in.readString();
        this.playingGameNum = in.readInt();
        this.finishedGameNum = in.readInt();
        this.exp = in.readInt();
        this.next_exp = in.readInt();
        this.level = in.readInt();
        this.task_count = in.readInt();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel source) {
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}
