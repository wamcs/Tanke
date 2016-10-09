package com.lptiyu.tanke.entity;

/**
 * Created by Jason on 2016/7/18.
 */
public class UpLoadGameRecord {
    public String uid;
    public String game_id;
    public String point_id;
    public String task_id;
    public String type;//游戏类型，个人赛还是团队赛
    public String point_statu;
    public String ranks_id;
    //    public String play_statu;


    @Override
    public String toString() {
        return "UpLoadGameRecord{" +
                "uid='" + uid + '\'' +
                ", game_id='" + game_id + '\'' +
                ", point_id='" + point_id + '\'' +
                ", task_id='" + task_id + '\'' +
                ", type='" + type + '\'' +
                ", point_statu='" + point_statu + '\'' +
                ", ranks_id='" + ranks_id + '\'' +
                '}';
    }
}
