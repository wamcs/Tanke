package com.lptiyu.tanke.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jason on 2016/10/17.
 */

public class TimeFormatUtils {
    private static final String PATTERN1 = "yyyy-MM-dd HH:mm:ss";

    // 将秒转化成时分秒--00:00:00
    public static String formatSecond(long second) {
        String hh = second / 3600 > 9 ? second / 3600 + "" : "0" + second / 3600;
        String mm = (second % 3600) / 60 > 9 ? (second % 3600) / 60 + "" : "0" + (second % 3600) / 60;
        String ss = (second % 3600) % 60 > 9 ? (second % 3600) % 60 + "" : "0" + (second % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    public static String formatSecondToMinute(long second) {
        return second / 60 + "分" + second % 60 + "秒";
    }

    public static String formatSecondToUsingTime(long second) {
        long sec = second % 60;
        if (second / 60 > 0) {
            long minute = second % 60;
            if (minute / 60 > 0) {
                long hour = minute % 60;
                if (hour / 24 > 0) {
                    long day = hour % 24;
                    return day + "天" + hour + "小时" + minute + "分钟" + sec + "秒";
                } else {
                    return hour + "小时" + minute + "分钟" + sec + "秒";
                }
            } else {
                return minute + "分" + sec + "秒";
            }
        } else {
            return sec + "秒";
        }
    }

    public static String caculateUsingTime(String dateMax, String dateMin) {
        Date date2 = parseDate(dateMax);
        Date date1 = parseDate(dateMin);
        if (date1 != null && date2 != null) {
            long milliSeconds = date2.getTime() - date1.getTime();
            return formatSecondToUsingTime(milliSeconds / 1000);
        }
        return null;
    }

    private static String dateFormat(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN1);
        return format.format(new Date(timeStamp));

    }

    private static Date parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(PATTERN1);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
