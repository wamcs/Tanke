package com.lptiyu.tanke.utils;


import android.content.Context;
import android.util.Log;

import com.lptiyu.tanke.R;
import com.lptiyu.tanke.global.AppData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

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

  public static final SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
  public static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
  public static final DateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
  public static final DateFormat timeFormatter2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
  public static final DateFormat timeFormatter3 = new SimpleDateFormat("mm:ss", Locale.CHINA);

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

  public static String getDateTimeWithoutYear(long time) {
    Date date = new Date(time);
    return String.format(
        AppData.getContext().getString(R.string.complete_time_without_year_formatter),
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes()
    );
  }

  public static String getDateTime(long time) {
    Date date = new Date(time);
    return String.format(
        AppData.getContext().getString(R.string.complete_time_formatter),
        date.getYear() + 1900,
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes()
    );
  }

  /**
   * @param date format was yyyy-MM-dd
   */
  public static Date parseDate(String date, DateFormat format) {
    if (date == null) return null;
    try {
      return format.parse(date);
    } catch (ParseException e) {
      return null;
    }
  }

  public static String parseCompleteTime(long time) {
    Long timestamp = Long.parseLong(String.valueOf(time)) * 1000;
    Date date = new Date(timestamp);
    return dateFormatter.format(date);
  }

  public static String parseFrontPartTime(long time) {
    Long timestamp = Long.parseLong(String.valueOf(time)) * 1000;
    Date data = new Date(timestamp);
    return timeFormatter2.format(data);
  }

  public static String parseLastPartTime(long time) {
    Long timestamp = Long.parseLong(String.valueOf(time)) * 1000;
    Date data = new Date(timestamp);
    return timeFormatter3.format(data);
  }

  public static String getConsumingTime(long time) {

    Timber.e("time : %d", time);

    long hour = time / ONE_HOUR_TIME;
    long minute = (time - hour * ONE_HOUR_TIME) / ONE_MINUTE_TIME;
    String formatter = AppData.getContext().getString(R.string.complete_game_consuming_time_formatter);
    return String.format(formatter, hour, minute);
  }


  /**
   * @param time was HH:mm:ss
   */
  public static Date parseTime(String time) {
    if (time == null || time.length() != 0) {
      return new Date();
    }
    try {
      return timeFormatter.parse(time);
    } catch (ParseException e) {
//      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parse time for GameDisplayFragment and GameDetailsActivity
   *
   * @param context
   * @param startDate
   * @param endDate
   * @param startTime
   * @param endTime
   * @return
   */
  public static String parseTime(final Context context,
                                 String startDate, String endDate,
                                 String startTime, String endTime) {
    String result;
    Calendar calendar = Calendar.getInstance();

    Date date = TimeUtils.parseDate(startDate, TimeUtils.dateFormatter);
    if (date == null) {
      result = "";
    } else {
      calendar.setTime(date);
      int _startMonth = calendar.get(Calendar.MONTH);
      int _startDate = calendar.get(Calendar.DATE);
      date = TimeUtils.parseDate(endDate, TimeUtils.dateFormatter);
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

  public static long getCurrentDate() {
    int year = getCurrentYear();
    int month = getCurrentMonth();
    int day = getCurrentDay();
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
    String date = year + "/" + month + "/" + day + " 00:00:00";
    Date date1 = null;
    try {
      date1 = sdf.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date1.getTime();
  }


  public static String parsePartTime(long time) {
    Long timestamp = Long.parseLong(String.valueOf(time)) * 1000;
    Date data = new Date(timestamp);
    return timeFormatter2.format(data);
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
