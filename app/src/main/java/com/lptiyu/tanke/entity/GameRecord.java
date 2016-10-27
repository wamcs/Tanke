package com.lptiyu.tanke.entity;

import com.lptiyu.tanke.entity.response.Jingwei;
import com.lptiyu.tanke.entity.response.TeamMember;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/7/20.
 */
public class GameRecord {
    public String game_id;
    public String join_time;
    public String start_time;
    public String last_task_ftime;
    public String line_id;
    public String play_statu;
    public String ranks_id;
    public String game_point_num;
    public String game_finish_point;
    public ThemeLine game_detail;
    public ArrayList<PointRecord> record_text;
    public ArrayList<TeamMember> team_member;
    public String team_id;
    public ArrayList<Jingwei> game_zone;

}
