package com.gameplatform.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    
    private static final long MINUTE_MILLIS = 60 * 1000;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 365 * DAY_MILLIS;
    
    /**
     * Format timestamp to relative time string
     * @param timestamp timestamp in milliseconds
     * @return formatted relative time string
     */
    public static String formatRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < HOUR_MILLIS) {
            long minutes = diff / MINUTE_MILLIS;
            return minutes + "分钟前";
        } else if (diff < DAY_MILLIS) {
            long hours = diff / HOUR_MILLIS;
            return hours + "小时前";
        } else if (diff < WEEK_MILLIS) {
            long days = diff / DAY_MILLIS;
            return days + "天前";
        } else if (diff < MONTH_MILLIS) {
            long weeks = diff / WEEK_MILLIS;
            return weeks + "周前";
        } else if (diff < YEAR_MILLIS) {
            long months = diff / MONTH_MILLIS;
            return months + "个月前";
        } else {
            long years = diff / YEAR_MILLIS;
            return years + "年前";
        }
    }
    
    /**
     * Format timestamp to date string
     * @param timestamp timestamp in milliseconds
     * @return formatted date string
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Format timestamp to date and time string
     * @param timestamp timestamp in milliseconds
     * @return formatted date and time string
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}