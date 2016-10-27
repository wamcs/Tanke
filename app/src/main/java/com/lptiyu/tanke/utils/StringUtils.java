package com.lptiyu.tanke.utils;

/**
 * Created by Jason on 2016/10/24.
 */

public class StringUtils {
    public static String getFileNameFromURL(String fileUrl) {
        if (fileUrl == null)
            return null;
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
