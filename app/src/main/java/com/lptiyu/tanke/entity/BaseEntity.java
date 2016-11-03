package com.lptiyu.tanke.entity;

import com.lptiyu.tanke.enums.PlayStatus;

/**
 * Created by Jason on 2016/10/21.
 */

public class BaseEntity {
    public int play_status = PlayStatus.NO_STATUS;
    public int cid;//0-附近游戏，1-线上可玩，3-定向乐跑，4-赛事活动，5-普通线下
    public String title;
}
