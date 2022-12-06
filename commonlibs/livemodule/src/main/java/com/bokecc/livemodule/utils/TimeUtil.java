package com.bokecc.livemodule.utils;

import android.content.Context;

import com.bokecc.livemodule.R;

/**
 * 时间相关工具类
 */
public class TimeUtil {

    /**
     * 将long时间转化为String
     *
     * @param oriTime long
     * @return string
     */
    public static String getFormatTime(long oriTime) {
        oriTime = oriTime / 1000; // 单位为毫秒
        StringBuilder sb = new StringBuilder();

        sb.append(getFillZero(oriTime / 60));
        sb.append(":");
        sb.append(getFillZero(oriTime % 60));

        return sb.toString();
    }

    /**
     * 填充数字
     *
     * @param number number
     * @return string
     */
    public static String getFillZero(long number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return String.valueOf(number);
        }
    }

    /**
     * 将毫秒转换为年月日时分秒
     */
    public static String getYearMonthDayHourMinuteSecond(Context mContext, long timeMillis) {
        int second = (int) (timeMillis % 60);// 秒
        long totalMinutes = timeMillis / 60;
        int minute = (int) (totalMinutes % 60);// 分
        long totalHours = totalMinutes / 60;
        int hour = (int) (totalHours % 24);// 时
        int day = (int) (totalHours / 24);
        if (day > 0) {
            return mContext.getString(R.string.string_utils_count_down_day_hour_minute_second,
                    day>99?99:day, hour, minute, second);
        } else if (hour > 0) {
            return mContext.getString(R.string.string_utils_count_down_hour_minute_second,
                    hour, minute, second);
        } else if (minute > 0) {
            return mContext.getString(
                    R.string.string_utils_count_down_minute_second, minute,
                    second);
        } else {
            return mContext.getString(R.string.string_utils_count_down_second,
                    second);
        }
    }

    public static String displayHHMMSS(int durationInSecond) {
        String display = "";
        String semicolon = ":";
        int h = durationInSecond / (60 * 60);
        int hs = durationInSecond % (60 * 60);
        int m = hs / 60;
        int s = hs % 60;
        String hours = "%d";
        String minute = "%d";
        String seconds = "%d";
        if (h < 10) {
            hours = "0" + hours;
        }
        if (m < 10) {
            minute = "0" + minute;
        }
        if (s < 10) {
            seconds = "0" + seconds;
        }
        display = hours + semicolon + minute + semicolon + seconds;
        return String.format(display, h, m, s);
    }
}
