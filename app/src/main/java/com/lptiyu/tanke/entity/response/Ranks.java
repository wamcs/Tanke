package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/10/20.
 */

public class Ranks implements Parcelable {
    @Override
    public String toString() {
        return "Ranks{" +
                "uid='" + uid + '\'' +
                ", log_num='" + log_num + '\'' +
                ", user_avatar='" + user_avatar + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", level='" + level + '\'' +
                ", is_myself=" + is_myself +
                '}';
    }

    public String uid;
    public String log_num;
    public String user_avatar;
    public String nick_name;
    public String level;
    public int is_myself;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.log_num);
        dest.writeString(this.user_avatar);
        dest.writeString(this.nick_name);
        dest.writeString(this.level);
        dest.writeInt(this.is_myself);
    }

    public Ranks() {
    }

    protected Ranks(Parcel in) {
        this.uid = in.readString();
        this.log_num = in.readString();
        this.user_avatar = in.readString();
        this.nick_name = in.readString();
        this.level = in.readString();
        this.is_myself = in.readInt();
    }

    public static final Parcelable.Creator<Ranks> CREATOR = new Parcelable.Creator<Ranks>() {
        @Override
        public Ranks createFromParcel(Parcel source) {
            return new Ranks(source);
        }

        @Override
        public Ranks[] newArray(int size) {
            return new Ranks[size];
        }
    };
}
