package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/10/12.
 */

public class UpLoadGameRecordResult implements Parcelable {

    public String task_finish_time;
    public int game_statu;
    public String get_exp;
    public String get_points;
    public String get_extra_points;
    public String get_extra_money;
    public String game_join_time;
    public String game_start_time;
    public String game_point_num;
    public int game_finish_point;
    public String game_id;
    public String uid;

    public int index;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.task_finish_time);
        dest.writeInt(this.game_statu);
        dest.writeString(this.get_exp);
        dest.writeInt(index);
        dest.writeString(this.get_points);
        dest.writeString(this.get_extra_points);
        dest.writeString(this.get_extra_money);
        dest.writeString(this.game_join_time);
        dest.writeString(this.game_start_time);
        dest.writeString(this.game_point_num);
        dest.writeInt(this.game_finish_point);
        dest.writeString(this.game_id);
        dest.writeString(this.uid);
    }

    public UpLoadGameRecordResult() {
    }

    protected UpLoadGameRecordResult(Parcel in) {
        this.task_finish_time = in.readString();
        this.index = in.readInt();
        this.game_statu = in.readInt();
        this.get_exp = in.readString();
        this.get_points = in.readString();
        this.get_extra_points = in.readString();
        this.get_extra_money = in.readString();
        this.game_join_time = in.readString();
        this.game_start_time = in.readString();
        this.game_point_num = in.readString();
        this.game_finish_point = in.readInt();
        this.game_id = in.readString();
        this.uid = in.readString();
    }

    public static final Parcelable.Creator<UpLoadGameRecordResult> CREATOR = new Parcelable
            .Creator<UpLoadGameRecordResult>() {
        @Override
        public UpLoadGameRecordResult createFromParcel(Parcel source) {
            return new UpLoadGameRecordResult(source);
        }

        @Override
        public UpLoadGameRecordResult[] newArray(int size) {
            return new UpLoadGameRecordResult[size];
        }
    };

    @Override
    public String toString() {
        return "UpLoadGameRecordResult{" +
                "task_finish_time='" + task_finish_time + '\'' +
                ", game_statu=" + game_statu +
                ", get_exp='" + get_exp + '\'' +
                ", get_points='" + get_points + '\'' +
                ", get_extra_points='" + get_extra_points + '\'' +
                ", get_extra_money='" + get_extra_money + '\'' +
                ", game_join_time='" + game_join_time + '\'' +
                ", game_start_time='" + game_start_time + '\'' +
                ", game_point_num='" + game_point_num + '\'' +
                ", game_finish_point=" + game_finish_point +
                ", game_id='" + game_id + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
