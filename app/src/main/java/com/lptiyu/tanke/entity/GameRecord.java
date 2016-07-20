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
    private Long id;
    private String join_time;
    private String start_time;
    private String last_task_ftime;
    private String play_statu;
    private String ranks_id;
    private String game_id;
    private String line_id;
    private String uid;
    private List<DBPointRecord> record_text;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.join_time);
        dest.writeString(this.start_time);
        dest.writeString(this.last_task_ftime);
        dest.writeString(this.play_statu);
        dest.writeString(this.ranks_id);
        dest.writeString(this.game_id);
        dest.writeString(this.line_id);
        dest.writeString(this.uid);
        dest.writeList(this.record_text);
    }

    public GameRecord() {
    }

    protected GameRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.join_time = in.readString();
        this.start_time = in.readString();
        this.last_task_ftime = in.readString();
        this.play_statu = in.readString();
        this.ranks_id = in.readString();
        this.game_id = in.readString();
        this.line_id = in.readString();
        this.uid = in.readString();
        this.record_text = new ArrayList<DBPointRecord>();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJoin_time() {
        return join_time;
    }

    public void setJoin_time(String join_time) {
        this.join_time = join_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLast_task_ftime() {
        return last_task_ftime;
    }

    public void setLast_task_ftime(String last_task_ftime) {
        this.last_task_ftime = last_task_ftime;
    }

    public String getPlay_statu() {
        return play_statu;
    }

    public void setPlay_statu(String play_statu) {
        this.play_statu = play_statu;
    }

    public String getRanks_id() {
        return ranks_id;
    }

    public void setRanks_id(String ranks_id) {
        this.ranks_id = ranks_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getLine_id() {
        return line_id;
    }

    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<DBPointRecord> getRecord_text() {
        return record_text;
    }

    public void setRecord_text(List<DBPointRecord> record_text) {
        this.record_text = record_text;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "id=" + id +
                ", join_time='" + join_time + '\'' +
                ", start_time='" + start_time + '\'' +
                ", last_task_ftime='" + last_task_ftime + '\'' +
                ", play_statu='" + play_statu + '\'' +
                ", ranks_id='" + ranks_id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", line_id='" + line_id + '\'' +
                ", uid='" + uid + '\'' +
                ", record_text=" + record_text +
                '}';
    }
}
