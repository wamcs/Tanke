package com.lptiyu.zxinglib.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/8/4.
 */
public class UploadGameRecordResponse implements Parcelable {
    /**
     * task_finish_time : 2016-08-04 15:35:37
     * game_statu : 2
     * get_exp : 3
     * game_join_time : 2016-08-03 15:54:10
     * game_start_time : 2016-08-04 14:53:26
     * game_point_num : 3
     * game_finish_point : 3
     * game_id : 2
     * uid : 10002
     */

    public String task_finish_time;
    public int game_statu;
    public String get_exp;
    public String game_join_time;
    public String game_start_time;
    public String game_point_num;
    public int game_finish_point;
    public String game_id;
    public String uid;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.task_finish_time);
        dest.writeInt(this.game_statu);
        dest.writeString(this.get_exp);
        dest.writeString(this.game_join_time);
        dest.writeString(this.game_start_time);
        dest.writeString(this.game_point_num);
        dest.writeInt(this.game_finish_point);
        dest.writeString(this.game_id);
        dest.writeString(this.uid);
    }

    public UploadGameRecordResponse() {
    }

    protected UploadGameRecordResponse(Parcel in) {
        this.task_finish_time = in.readString();
        this.game_statu = in.readInt();
        this.get_exp = in.readString();
        this.game_join_time = in.readString();
        this.game_start_time = in.readString();
        this.game_point_num = in.readString();
        this.game_finish_point = in.readInt();
        this.game_id = in.readString();
        this.uid = in.readString();
    }

    public static final Creator<UploadGameRecordResponse> CREATOR = new Creator<UploadGameRecordResponse>() {
        @Override
        public UploadGameRecordResponse createFromParcel(Parcel source) {
            return new UploadGameRecordResponse(source);
        }

        @Override
        public UploadGameRecordResponse[] newArray(int size) {
            return new UploadGameRecordResponse[size];
        }
    };

    @Override
    public String toString() {
        return "UploadGameRecordResponse{" +
                "task_finish_time='" + task_finish_time + '\'' +
                ", game_statu=" + game_statu +
                ", get_exp='" + get_exp + '\'' +
                ", game_join_time='" + game_join_time + '\'' +
                ", game_start_time='" + game_start_time + '\'' +
                ", game_point_num='" + game_point_num + '\'' +
                ", game_finish_point=" + game_finish_point +
                ", game_id='" + game_id + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
