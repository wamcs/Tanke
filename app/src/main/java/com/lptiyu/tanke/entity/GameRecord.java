package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.database.DBPointRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2016/7/20.
 */
public class GameRecord implements Parcelable {
    //    public Long id;
    public String game_id;
    public String join_time;
    //    public String start_time;
    public String last_task_ftime;
    public String line_id;
    public String play_statu;
    public String ranks_id;
    //    public String uid;
    public List<PointRecord> record_text;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //        dest.writeValue(this.id);
        dest.writeString(this.join_time);
        //        dest.writeString(this.start_time);
        dest.writeString(this.last_task_ftime);
        dest.writeString(this.play_statu);
        dest.writeString(this.ranks_id);
        dest.writeString(this.game_id);
        dest.writeString(this.line_id);
        //        dest.writeString(this.uid);
        dest.writeList(this.record_text);
    }

    public GameRecord() {
    }

    protected GameRecord(Parcel in) {
        //        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.join_time = in.readString();
        //        this.start_time = in.readString();
        this.last_task_ftime = in.readString();
        this.play_statu = in.readString();
        this.ranks_id = in.readString();
        this.game_id = in.readString();
        this.line_id = in.readString();
        //        this.uid = in.readString();
        this.record_text = new ArrayList<PointRecord>();
        in.readList(this.record_text, DBPointRecord.class.getClassLoader());
    }

    public static final Parcelable.Creator<GameRecord> CREATOR = new Parcelable.Creator<GameRecord>() {
        @Override
        public GameRecord createFromParcel(Parcel source) {
            return new GameRecord(source);
        }

        @Override
        public GameRecord[] newArray(int size) {
            return new GameRecord[size];
        }
    };

    @Override
    public String toString() {
        return "GameRecord{" +
                //                "id=" + id +
                ", join_time='" + join_time + '\'' +
                //                ", start_time='" + start_time + '\'' +
                ", last_task_ftime='" + last_task_ftime + '\'' +
                ", play_statu='" + play_statu + '\'' +
                ", ranks_id='" + ranks_id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", line_id='" + line_id + '\'' +
                //                ", uid='" + uid + '\'' +
                ", record_text=" + record_text +
                '}';
    }
}
