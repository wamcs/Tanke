package com.lptiyu.tanke.utils;

import android.content.Context;

import com.lptiyu.tanke.widget.dialog.DatePickerDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

    private SweetAlertDialog getDatePickerDialog(Context context) {
        return new DatePickerDialog(context);
    }
}
