package com.lptiyu.tanke.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/23
 *
 * @author ldx
 */
public class UserDetails implements Parcelable {

    public String name;
    public String img;
    public String phone;
    public String birthday;
    public String sex;
    public String high;
    public String weight;
    public String address;
    public int num;
    public int finish_num;
    public int experience;
    public int experiencelast;
    public int level;
    public int task_count;
    public String money;
    public String points;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.img);
        dest.writeString(this.phone);
        dest.writeString(this.birthday);
        dest.writeString(this.sex);
        dest.writeString(this.high);
        dest.writeString(this.weight);
        dest.writeString(this.address);
        dest.writeInt(this.num);
        dest.writeInt(this.finish_num);
        dest.writeInt(this.experience);
        dest.writeInt(this.experiencelast);
        dest.writeInt(this.level);
        dest.writeInt(this.task_count);
        dest.writeString(this.money);
        dest.writeString(this.points);
    }

    public UserDetails() {
    }

    protected UserDetails(Parcel in) {
        this.name = in.readString();
        this.img = in.readString();
        this.phone = in.readString();
        this.birthday = in.readString();
        this.sex = in.readString();
        this.high = in.readString();
        this.weight = in.readString();
        this.address = in.readString();
        this.num = in.readInt();
        this.finish_num = in.readInt();
        this.experience = in.readInt();
        this.experiencelast = in.readInt();
        this.level = in.readInt();
        this.task_count = in.readInt();
        this.money = in.readString();
        this.points = in.readString();
    }

    public static final Parcelable.Creator<UserDetails> CREATOR = new Parcelable.Creator<UserDetails>() {
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
