package com.lptiyu.tanke.gameplaying.records;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.lptiyu.tanke.BuildConfig;

import java.lang.reflect.Type;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/3/5
 *
 * @author ldx
 */
public class RunningRecord implements Parcelable {

    private int index;

    @SerializedName("ranks_id")
    private long teamId;

    @SerializedName("point_id")
    private long pointId;

    @SerializedName("task_id")
    private long taskId;

    private int distance;

    private String x;

    private String y;

    @SerializedName("create_time")
    private long createTime;

    private RECORD_TYPE state;

    private RunningRecord(Builder builder) {
        setIndex(builder.index);
        setTeamId(builder.teamId);
        setPointId(builder.pointId);
        setTaskId(builder.taskId);
        setDistance(builder.distance);
        setX(builder.x);
        setY(builder.y);
        setCreateTime(builder.createTime);
        setState(builder.state);
    }

    public enum RECORD_TYPE implements JsonSerializer<RECORD_TYPE>, JsonDeserializer<RECORD_TYPE> {
        GAME_START(1),
        POINT_REACH(2),
        TASK_START(3),
        TASK_FINISH(4),
        POINT_FINISH(5),
        GAME_FINISH(6);

        public final int type;

        public int getType() {
            return type;
        }

        RECORD_TYPE(int i) {
            type = i;
        }

        @Override
        public RECORD_TYPE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
                JsonParseException {
            final int item = json.getAsInt();
            for (RECORD_TYPE state : RECORD_TYPE.values()) {
                if (state.getType() == item) {
                    return state;
                }
            }

            if (BuildConfig.DEBUG) {
                throw new IllegalStateException(
                        String.format("The item (%d) for RECORD_TYPE is unexpected.",
                                item));
            }
            return TASK_FINISH;
        }

        @Override
        public JsonElement serialize(RECORD_TYPE src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.type);
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public RECORD_TYPE getState() {
        return state;
    }

    public void setState(RECORD_TYPE state) {
        this.state = state;
    }

    public LatLng getLatLng() {
        return new LatLng(Double.valueOf(x), Double.valueOf(y));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeLong(this.teamId);
        dest.writeLong(this.pointId);
        dest.writeLong(this.taskId);
        dest.writeInt(this.distance);
        dest.writeString(this.x);
        dest.writeString(this.y);
        dest.writeLong(this.createTime);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
    }

    public RunningRecord() {
    }

    protected RunningRecord(Parcel in) {
        this.index = in.readInt();
        this.teamId = in.readLong();
        this.pointId = in.readLong();
        this.taskId = in.readLong();
        this.distance = in.readInt();
        this.x = in.readString();
        this.y = in.readString();
        this.createTime = in.readLong();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : RECORD_TYPE.values()[tmpState];
    }

    public static final Creator<RunningRecord> CREATOR = new Creator<RunningRecord>() {
        @Override
        public RunningRecord createFromParcel(Parcel source) {
            return new RunningRecord(source);
        }

        @Override
        public RunningRecord[] newArray(int size) {
            return new RunningRecord[size];
        }
    };

    public static final class Builder {
        private int index;
        private long teamId;
        private long pointId;
        private long taskId;
        private int distance;
        private String x;
        private String y;
        private long createTime;
        private RECORD_TYPE state;

        public Builder() {
        }

        public Builder index(int val) {
            index = val;
            return this;
        }

        public Builder teamId(long val) {
            teamId = val;
            return this;
        }

        public Builder pointId(long val) {
            pointId = val;
            return this;
        }

        public Builder taskId(long val) {
            taskId = val;
            return this;
        }

        public Builder distance(int val) {
            distance = val;
            return this;
        }

        public Builder x(String val) {
            x = val;
            return this;
        }

        public Builder y(String val) {
            y = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder state(RECORD_TYPE val) {
            state = val;
            return this;
        }

        public RunningRecord build() {
            return new RunningRecord(this);
        }
    }

    @Override
    public String toString() {
        return "RunningRecord{" +
                "index=" + index +
                ", teamId=" + teamId +
                ", pointId=" + pointId +
                ", taskId=" + taskId +
                ", distance=" + distance +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", createTime=" + createTime +
                ", state=" + state +
                '}';
    }
}
