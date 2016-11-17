package com.lptiyu.tanke.enums;

/**
 * Created by Jason on 2016/8/19.
 */
public interface GameState {
    int ALPHA_TEST = 1;
    int MAINTAINING = 2;
    int FINISHED = 3;
    int NORMAL = 4;

    String ALPHA_TEST_STR = "内测";
    String MAINTAINING_STR = "维护中";
    String FINISHED_STR = "已下线";
    String NORMAL_STR = "普通";
}
