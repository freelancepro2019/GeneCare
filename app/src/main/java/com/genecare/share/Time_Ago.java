package com.genecare.share;

import android.content.Context;

import com.genecare.R;

import java.util.Calendar;

public class Time_Ago {
    private static int second = 1;
    private static int minute = second * 60;
    private static int hour = minute * 60;
    private static int day = hour * 24;

    public static String getTimeAgo(long time, Context context) {
        long now = (Calendar.getInstance().getTimeInMillis())/1000;
        if (time <= 0||time>now) {
            return null;
        }
        time=now-time;
        if (time < minute) {
            return context.getString(R.string.just_now);

        } else if (time < 2 * minute) {
            return context.getString(R.string.a_minute_ago);

        } else if (time < hour) {
            return time / minute + " " + context.getString(R.string.min);

        } else if (time < 2 * hour) {
            return context.getString(R.string.an_hour_ago);

        } else if (time < day) {
            return time / hour + " " + context.getString(R.string.hs);

        } else if (time < 2 * day) {
            return context.getString(R.string.yesterday);
        } else {
            return time / day + " " + context.getString(R.string.day);
        }

    }

}
