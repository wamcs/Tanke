package com.lptiyu.zxinglib.android.entity;

/**
 * Created by Jason on 2016/7/18.
 */
public class UpLoadGameRecord {
    public int status;
    public String info;
    public UploadGameRecordResponse data;

    @Override
    public String toString() {
        return "UpLoadGameRecord{" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
