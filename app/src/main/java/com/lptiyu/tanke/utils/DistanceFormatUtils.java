package com.lptiyu.tanke.utils;

import java.text.DecimalFormat;

/**
 * Created by Jason on 2016/8/18.
 */
public class DistanceFormatUtils {
    private static DecimalFormat df = new java.text.DecimalFormat("0.00");

    public static String formatMeterToKiloMeter(double meter) {
        return df.format(meter / 1000d);
    }
}
