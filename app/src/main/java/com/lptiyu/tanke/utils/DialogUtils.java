package com.lptiyu.tanke.utils;

/**
 * Created by Jason on 2016/7/1.
 */

public class DialogUtils {
    private static DialogUtils dialog;

    private DialogUtils() {
    }

    public static DialogUtils getInstance() {
        if (dialog == null) {
            return new DialogUtils();
        } else {
            return dialog;
        }
    }

}
