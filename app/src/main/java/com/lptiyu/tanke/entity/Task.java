package com.lptiyu.tanke.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.lptiyu.tanke.enums.PointTaskStatus;

/**
 * Created by Jason on 2016/8/2.
 */
public class Task implements Comparable<Task>, Parcelable {
    /**
     * id : 5
     * point_id : 5
     * content : <html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"
     * /></head><body><p>抱着一丝希望我来到体育馆寻找保安口中的那个小伙子。</p>
     * <p>
     * <p>走进馆内有一群少年在打球我便拿着照片询问：</p>
     * <p>
     * <p>&ldquo;请问照片中的姑娘经常来这里看打球吗？&rdquo;</p>
     * <p>
     * <p>一少年顿了3秒回答说：&ldquo;哦，这不是宇飞的女朋嘛。&rdquo;</p>
     * <p>
     * <p>我接着问道：&ldquo;那你能告诉我宇飞在哪里吗？&rdquo;</p>
     * <p>
     * <p>少年：&ldquo;宇飞一个数字控，从来不直接说地点，<strong>今天他走的时候留下了两个数字12、8。或许你在攀岩馆右侧的牌匾中找到他的位置。&rdquo;</strong></p>
     * <p>
     * <p><strong>（回答地点3个字）-- -- --</strong></p>
     * <p>
     * <p>&nbsp;</p>
     * </body></html>
     * type : 1
     * pwd : 114.409770,30.525856
     * task_index : 1
     * next_task :
     */

    public String id;
    public String point_id;
    public String content;
    public String type;
    public String pwd;
    public String task_index;
    public String next_task;

    public int state = PointTaskStatus.UNSTARTED;//默认没有完成
    public String finishTime = "";
    public String exp;

    @Override
    public int compareTo(Task another) {
        if (another != null) {
            if (Integer.parseInt(this.task_index) > Integer.parseInt(another.task_index)) {
                return 1;
            } else if (Integer.parseInt(this.task_index) == Integer.parseInt(another.task_index)) {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.point_id);
        dest.writeString(this.content);
        dest.writeString(this.type);
        dest.writeString(this.pwd);
        dest.writeString(this.task_index);
        dest.writeString(this.next_task);
        dest.writeInt(this.state);
        dest.writeString(this.finishTime);
        dest.writeString(this.exp);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.id = in.readString();
        this.point_id = in.readString();
        this.content = in.readString();
        this.type = in.readString();
        this.pwd = in.readString();
        this.task_index = in.readString();
        this.next_task = in.readString();
        this.state = in.readInt();
        this.finishTime = in.readString();
        this.exp = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
