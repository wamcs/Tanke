package com.lptiyu.tanke.utils;

import java.text.DecimalFormat;

/**
 * Created by Jason on 2016/8/18.
 */
public class DistanceFormatUtils {
    private static DecimalFormat df = new java.text.DecimalFormat("#.00");

    public static String formatMeter(double meter) {
        if (meter < 1000) {
            return df.format(meter) + "米";
        } else {
            return df.format(meter / (double) 1000) + "千米";
        }
    }
}
