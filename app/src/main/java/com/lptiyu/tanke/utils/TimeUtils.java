package com.lptiyu.tanke.utils;

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

  public final static long ONE_MINUTE_TIME = 60000L;
  public final static long ONE_HOUR_TIME = ONE_MINUTE_TIME * 60;
  public final static long ONE_DAY_TIME = ONE_HOUR_TIME * 24;

  public static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
  public static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
  public static final DateFormat timeFormatter2 = new SimpleDateFormat("HH:mm", Locale.CHINA);


  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);

  public static String getStandardTime(long timeStamp) {
    Date date = new Date(timeStamp);
    return sdf.format(date);
  }

  public static Date getStandardDate(long timeStamp) {
    return new Date(timeStamp);
  }

  public static long getTimeStamp(String standardTime) {
    Date date = new Date();
    try {
      date = sdf.parse(standardTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date.getTime();
  }


  public static String getFriendlyTime(long time) {
    if (ONE_MINUTE_TIME > time) {
      return String.format("00 : %2d", time / 1000);
    }
    if (ONE_HOUR_TIME > time) {
      int min = (int) (time / ONE_MINUTE_TIME);
      int sec = (int) ((time % ONE_MINUTE_TIME) / 1000);
      return String.format("%02d : %02d", min, sec);
    }
    if (ONE_DAY_TIME > time) {
      return String.format("大约还有%d小时", time / ONE_HOUR_TIME);
    }
    return "大于一天";
  }

  /*
  **
  * @param date format was yyyy-MM-dd
  */
  public static Date parseDate(String date) {
    try {
      return dateFormatter.parse(date);
    } catch (ParseException e) {
//      e.printStackTrace();
      return null;
    }
  }

  /**
   * @param time was yyyy-MM-dd or HH:mm:ss
   */
  public static Date parseTime(String time) {
    try {
      return timeFormatter.parse(time);
    } catch (ParseException e) {
//      e.printStackTrace();
      return null;
    }
  }

  public static String formatHourMinute(String content) {
    Date parsed = parseTime(content);
    if (parsed == null) {
      return null;
    }
    return timeFormatter2.format(parsed);
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

  public static int getCurrentMinute() {
    return Calendar.getInstance().get(Calendar.MINUTE);
  }


  /**
   * the follow three function are used for getting time (hours:minutes:seconds)
   */
  public static String getSeconds(long time) {
    int s = (int) (time / 1000) % 60;
    if (s < 10)
      return "0" + s;
    else
      return s + "";
  }

  public static String getMinutes(long time) {

    //总分钟数

    int m = ((int) (time / 1000) / 60) % 60;
    if (m < 10)
      return "0" + m;
    else
      return m + "";
  }

  public static String getHours(long time) {
    int h = (int) (time / 1000) / 3600;
    if (h < 0)
      return "0" + h;
    else
      return h + "";
  }

}
