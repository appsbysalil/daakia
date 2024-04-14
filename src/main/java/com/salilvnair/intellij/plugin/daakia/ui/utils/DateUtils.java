package com.salilvnair.intellij.plugin.daakia.ui.utils;

import java.text.SimpleDateFormat;

public class DateUtils {
    private DateUtils() {}

    public static String todayAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    public static String yearAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(new java.util.Date());
    }

    public static String yearFromDateString(String dateString) {
        return dateString.substring(0, 4);
    }
}
