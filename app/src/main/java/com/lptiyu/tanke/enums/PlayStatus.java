package com.lptiyu.tanke.enums;

/**
 * Created by Jason on 2016/7/18.
 */
public interface PlayStatus {
    int NO_STATUS = -2;
    int NEVER_ENTER_GANME = -1;
    int HAVE_ENTERED_bUT_NOT_START_GAME = 0;
    int HAVE_STARTED_GAME = 1;
    int GAME_OVER = 2;
}
