package com.lptiyu.tanke.pojo;

/**
 * Created by Jason on 2016/7/18.
 */
public class UpLoadGameRecord {
    public long uid;
    public long game_id;
    public long point_id;
    public long task_id;
    public long type;//游戏类型，个人赛还是团队赛
    public long point_statu;
    public long play_statu;

    @Override
    public String toString() {
        return "UpLoadGameRecord{" +
                "uid=" + uid +
                ", game_id=" + game_id +
                ", point_id=" + point_id +
                ", task_id=" + task_id +
                ", type=" + type +
                ", point_statu=" + point_statu +
                ", play_statu=" + play_statu +
                '}';
    }
}
