package com.learn.jk.Utils;

import com.learn.jk.data.GLOBAL;
import com.learn.jk.data.model.UserModel;

import java.util.Date;

public class DateAndTimeUtils {
    public static final int SECOND_MILLIS = 1000;
    public static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final String CURRENT_VERSION = "1.0.0";

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < GLOBAL.MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * GLOBAL.MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * GLOBAL.MINUTE_MILLIS) {
            return diff / GLOBAL.MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * GLOBAL.MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * GLOBAL.HOUR_MILLIS) {
            return diff / GLOBAL.HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * GLOBAL.HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / GLOBAL.DAY_MILLIS + " days ago";
        }
    }
    public static String getDateType(String dateType, String day,String month,String year) {
        String strBirthday=null;
        switch (dateType) {
            case "Show my birthday":
                strBirthday = month + " " + day + "," + year;
                break;
            case "Show month and day only":
                strBirthday = month + " " + day;
                break;
            case "don't show my birthday":
                strBirthday=null;
                break;
            default:
                strBirthday=null;
                break;
        }
        return strBirthday;
    }
}
