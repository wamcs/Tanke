package com.lptiyu.tanke.utils.xutils3;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by Jason on 2016/8/12.
 */
public class SwipeRefreshLayoutUtils {
    /**
     * 顶部刷新的样式
     *
     * @param swipe
     */
    public static void setSwipeStyle(SwipeRefreshLayout swipe) {
        swipe.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R
                .color.holo_blue_bright, android.R.color.holo_orange_light);
    }
}
