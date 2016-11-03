package com.lptiyu.tanke.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jason on 2016/10/31.
 */

public class DateFormatterUtils {
    public static final String yyyyMMdd = "yyyy-MM-dd";

    public static String parseTimeStamp(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat(yyyyMMdd);
        Date date = new Date(timeStamp * 1000);//后台返回的是秒数，Android中格式化时间戳传的是毫秒数
        return format.format(date);
    }
}
