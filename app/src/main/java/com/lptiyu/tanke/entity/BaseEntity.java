package com.lptiyu.tanke.entity;

import com.lptiyu.tanke.enums.PlayStatus;

/**
 * Created by Jason on 2016/10/21.
 */

public class BaseEntity {
    public int play_status = PlayStatus.NO_STATUS;
    public int cid;
    public String title;
}
