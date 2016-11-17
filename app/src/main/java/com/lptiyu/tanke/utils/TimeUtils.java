package com.lptiyu.tanke.utils;


import android.content.Context;

import com.lptiyu.tanke.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * author:wamcs
 * date:2016/5/25
 * email:kaili@hustunique.com
 */
public class TimeUtils {

    public static final long ONE_SECOND_TIME = 1000L;
    public final static long ONE_MINUTE_TIME = 60 * ONE_SECOND_TIME;
    public final static long ONE_HOUR_TIME = ONE_MINUTE_TIME * 60;
    public final static long ONE_DAY_TIME = ONE_HOUR_TIME * 24;

    public static final String PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN2 = "yyyy-MM-dd";
    public static final String PATTERN3 = "HH:mm:ss";
    public static final String PATTERN4 = "HH:mm";
    public static final String PATTERN5 = "mm:ss";

    public static DateFormat getDateFormatter(String pattern) {
        return new SimpleDateFormat(pattern, Locale.CHINA);
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateStrToStamp(String dateStr, String pattern) {
        String res = null;
        try {
            Date date = getDateFormatter(pattern).parse(dateStr);
            long ts = date.getTime();
            res = String.valueOf(ts);
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDateStr(String stamp, String pattern) {
        String res;
        long lt = new Long(stamp);
        Date date = new Date(lt);
        res = getDateFormatter(pattern).format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDateStr(long stamp, String pattern) {
        String res;
        Date date = new Date(stamp);
        res = getDateFormatter(pattern).format(date);
        return res;
    }

    /**
     * 定向乐跑中，将后台返回的秒数转化为时长
     * <p>
     * 将秒转化成时分秒--00:00:00
     */
    public static String formatSecond(long second) {
        String hh = second / 3600 > 9 ? second / 3600 + "" : "0" + second / 3600;
        String mm = (second % 3600) / 60 > 9 ? (second % 3600) / 60 + "" : "0" + (second % 3600) / 60;
        String ss = (second % 3600) % 60 > 9 ? (second % 3600) % 60 + "" : "0" + (second % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    public static String caculateUsingTime(String endDateStr, String startDateStr) {
        Date startDate = null;
        Date endDate = null;
        try {
            endDate = getDateFormatter(PATTERN1).parse(endDateStr);
            startDate = getDateFormatter(PATTERN1).parse(startDateStr);
            if (startDate != null && endDate != null) {
                return parseSecond2Duration((endDate.getTime() - startDate.getTime()) / 1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析注册时验证码的倒计时
     *
     * @param time
     * @return
     */
    public static String getFriendlyTime(long time) {
        if (ONE_MINUTE_TIME > time) {
            return String.format("00 : %2d".toLowerCase(), time / 1000);
        }
        if (ONE_HOUR_TIME > time) {
            int min = (int) (time / ONE_MINUTE_TIME);
            int sec = (int) ((time % ONE_MINUTE_TIME) / 1000);
            return String.format("%02d : %02d".toLowerCase(), min, sec);
        }
        if (ONE_DAY_TIME > time) {
            return String.format("大约还有%d小时".toLowerCase(), time / ONE_HOUR_TIME);
        }
        return "大于一天";
    }

    /**
     * 将年月日字符串变成Date对象
     *
     * @param date format was yyyy-MM-dd
     */
    private static Date parseDate(String date, DateFormat format) {
        if (date == null)
            return null;
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 解析游戏详情里面的时间
     *
     * @param context
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @return
     */
    public static String parseTime(Context context,
                                   String startDate, String endDate,
                                   String startTime, String endTime) {
        String[] result = new String[]{"", ""};
        String timeResult = "";
        Calendar calendar = Calendar.getInstance();

        Date date = parseDate(startDate, getDateFormatter(PATTERN2));
        if (date != null) {
            calendar.setTime(date);
            int _startMonth = calendar.get(Calendar.MONTH) + 1;
            int _startDate = calendar.get(Calendar.DATE);
            date = parseDate(endDate, getDateFormatter(PATTERN2));
            calendar.setTime(date);
            int _endMonth = calendar.get(Calendar.MONTH) + 1;
            int _endDate = calendar.get(Calendar.DATE);
            result[0] = String.format(Locale.CHINA, context.getString(R.string.main_page_date_format_pattern),
                    _startMonth, _startDate, _endMonth, _endDate);
            String.format(Locale.CHINA, context.getString(R.string.main_page_date_format_pattern),
                    _startMonth, _startDate, _endMonth, _endDate);
        }

        if (startTime == null || startTime.equals("")) {
            result[1] = context.getString(R.string.main_page_forever);
            timeResult = context.getString(R.string.main_page_forever);
        } else {
            result[1] = startTime + "-" + endTime;
            timeResult = startTime + "-" + endTime;
        }
        return timeResult;
    }


    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 将秒数转化为x小时x分x秒
     *
     * @param seconds
     * @return
     */
    public static String parseSecond2Duration(long seconds) {
        if (seconds < 60) {
            return seconds + "秒";
        }
        if (seconds >= 60 && seconds < 3600) {
            return seconds / 60 + "分" + seconds % 60 + "秒";
        } else {
            return seconds / 3600 + "小时" + (seconds % 3600) / 60 + "分" + seconds % 60 + "秒";
        }
    }

    /**
     * 格式化平均配速
     *
     * @param peisu
     * @return
     */
    public static String parsePeisu(long peisu) {
        if (peisu < 60) {
            return peisu + "\"";
        }
        if (peisu >= 60 && peisu < 3600) {
            return peisu / 60 + "\'" + peisu % 60 + "\"";
        } else {
            return peisu / 3600 + "\'" + (peisu % 3600) / 60 + "\'" + peisu % 60 + "\"";
        }
    }
}
