package com.lptiyu.tanke.entity.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jason on 2016/11/11.
 */

public class DRRecordEntity implements Parcelable {

    /**
     * id : 130
     * game_id : 63
     * start_time : 1476758772
     * end_time : 1476759091
     * distance : 23.00
     * title : 定向乐跑测试路线
     * cover : http://test.lptiyu.com/run/Public/Upload/pic/game/2016-10-11/thumb_57fcb36c1fffb.png
     * tag : 定向乐跑
     */

    public String id;
    public String game_id;
    public String start_time;
    public String end_time;
    public String distance;
    public String title;
    public String cover;
    public String tag;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.game_id);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(this.distance);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.tag);
    }

    public DRRecordEntity() {
    }

    protected DRRecordEntity(Parcel in) {
        this.id = in.readString();
        this.game_id = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.distance = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.tag = in.readString();
    }

    public static final Parcelable.Creator<DRRecordEntity> CREATOR = new Parcelable.Creator<DRRecordEntity>() {
        @Override
        public DRRecordEntity createFromParcel(Parcel source) {
            return new DRRecordEntity(source);
        }

        @Override
        public DRRecordEntity[] newArray(int size) {
            return new DRRecordEntity[size];
        }
    };
}
