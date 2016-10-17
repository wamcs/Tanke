package com.lptiyu.tanke.utils;

/**
 * Created by Jason on 2016/10/12.
 */

public class PicUtils {
    public static String getPicNameFromUrl(String picUrl) {
        return picUrl.substring(picUrl.lastIndexOf('/'));
    }
}
