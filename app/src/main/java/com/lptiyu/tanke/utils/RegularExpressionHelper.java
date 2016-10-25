package com.lptiyu.tanke.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jason on 2016/9/2.
 */
public class RegularExpressionHelper {
    /**
     * 正则表达式判断是否为手机号码
     *
     * @param input 要匹配的字符串
     * @return
     */
    public static boolean isPhoneNumber(String input) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
