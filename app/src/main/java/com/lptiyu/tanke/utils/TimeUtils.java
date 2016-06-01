package com.lptiyu.tanke.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * EMAIL : danxionglei@foxmail.com
 * DATE : 16/5/29
 *
 * @author ldx
 */
public class TimeUtils {

  public static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

  public static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

  public static final DateFormat timeFormatter2 = new SimpleDateFormat("HH:mm", Locale.CHINA);

  /**
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
   * @param time  was yyyy-MM-dd or HH:mm:ss
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

}
