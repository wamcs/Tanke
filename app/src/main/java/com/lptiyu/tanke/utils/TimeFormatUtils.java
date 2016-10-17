package com.lptiyu.tanke.utils;

/**
 * Created by Jason on 2016/10/17.
 */

public class TimeFormatUtils {
    // 将秒转化成时分秒--00:00:00
    public static String formatSecond(long second) {
        String hh = second / 3600 > 9 ? second / 3600 + "" : "0" + second / 3600;
        String mm = (second % 3600) / 60 > 9 ? (second % 3600) / 60 + "" : "0" + (second % 3600) / 60;
        String ss = (second % 3600) % 60 > 9 ? (second % 3600) % 60 + "" : "0" + (second % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    public static String formatSecondToMinute(int second) {
        return second / 60 + "分" + second % 60 + "秒";
    }

    public static String formatSecondToMinute(long second) {
        return second / 60 + "分" + second % 60 + "秒";
    }
}
