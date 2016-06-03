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
   * @param time was HH:mm:ss
   */
  public static Date parseTime(String time) {
    try {
      return timeFormatter.parse(time);
    } catch (ParseException e) {
//      e.printStackTrace();
      return null;
    }
  }

  public static String parseTime(final Context context,
                                 String startDate, String endDate,
                                 String startTime, String endTime) {
    String result;
    Calendar calendar = Calendar.getInstance();

    Date date = TimeUtils.parseDate(startDate);
    if (date == null) {
      result = "";
    } else {
      calendar.setTime(date);
      int _startMonth = calendar.get(Calendar.MONTH);
      int _startDate = calendar.get(Calendar.DATE);
      date = TimeUtils.parseDate(endDate);
      calendar.setTime(date);
      int _endMonth = calendar.get(Calendar.MONTH);
      int _endDate = calendar.get(Calendar.DATE);
      result = String.format(Locale.CHINA, context.getString(R.string.main_page_date_format_pattern),
          _startMonth, _startDate, _endMonth, _endDate);
    }

    Date time = TimeUtils.parseTime(startTime);
    if (time == null) {
      result += context.getString(R.string.main_page_forever);
    } else {
      result += TimeUtils.formatHourMinute(startTime);
      result += "-";
      result += TimeUtils.formatHourMinute(endTime);
    }

    return result;
  }


  public static String formatHourMinute(String content) {
    Date parsed = parseTime(content);
    if (parsed == null) {
      return null;
    }
    return timeFormatter2.format(parsed);
  }

}
