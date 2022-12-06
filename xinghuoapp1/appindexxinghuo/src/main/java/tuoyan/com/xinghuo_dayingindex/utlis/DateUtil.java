package tuoyan.com.xinghuo_dayingindex.utlis;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/1/29.
 */
public class DateUtil {

    public static SimpleDateFormat DATE_FORMAT_TILL_SECOND = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CURRENT_YEAR = new SimpleDateFormat("MM-dd");
    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CH = new SimpleDateFormat("yyyy-MM-dd");

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CH_CHAR = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CH_POINT = new SimpleDateFormat("yyyy.MM.dd");
    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CURRENT_YEAR_POINT = new SimpleDateFormat("MM.dd");
    public static SimpleDateFormat DATE_FORMAT_TILL_DAY_CURRENT_YEAR_COLON = new SimpleDateFormat("HH:mm");


    /**
     * "yyyy-MM-dd HH:mm:ss"->"yyyy年MM月dd日"
     *
     * @param dateStr "yyyy-MM-dd HH:mm:ss"
     * @param sdf 格式
     * @return "yyyy年MM月dd日"
     */

    public static String strToformatCH(String dateStr, SimpleDateFormat sdf) {
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                Date date = DATE_FORMAT_TILL_SECOND.parse(dateStr);
                return sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String longToString(String longDate) throws ParseException {
       Date dt = longToDate(Long.valueOf(longDate), "yyyy-MM-dd");
        return DATE_FORMAT_TILL_SECOND.format(dt);
    }
    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }
    /**
     * 日期逻辑:1秒前,分钟 小时 ......3天前(最早到3天前)
     *
     * @param dateStr 日期字符串
     * @return
     */
    public static String timeLogic(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        Date date = null;
        try {
            date = strToDate(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return dateStr;
        }
        try {
            calendar.setTime(date);
        } catch (Exception e) {
            return dateStr;
        }
        long past = calendar.getTimeInMillis();
        // 相差的秒数
        long time = (now - past) / 1000;

        StringBuffer sb = new StringBuffer();
        if (time > 0 && time < 60) { // 1小时内
            return sb.append(time + "秒前").toString();
        }
        if (time > 60 && time < 3600) {
            return sb.append(time / 60 + "分钟前").toString();
        }
        if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600 + "小时前").toString();
        }
        if (time >= 3600 * 24) {
            int dayNum = (int) Math.floor(time / (3600 * 24));
            if (dayNum >= 3) {
                dayNum = 3;
            }
            return sb.append(dayNum + "天前").toString();
        }
        return dateStr;

//        return "时间获取中";
    }

    /**
     * 日期逻辑:1秒前,分钟 小时 ......可以到n天前
     *
     * @param dateStr 日期字符串
     * @return
     */
    public static String timeLogicOld(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        Date date = null;
        try {
            date = strToDate(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return dateStr;
        }
        try {
            calendar.setTime(date);
        } catch (Exception e) {
            return dateStr;
        }
        long past = calendar.getTimeInMillis();
        // 相差的秒数
        long time = (now - past) / 1000;

        StringBuffer sb = new StringBuffer();
        if (time > 0 && time < 60) { // 1小时内
            return sb.append(time + "秒前").toString();
        } else if (time > 60 && time < 3600) {
            return sb.append(time / 60 + "分钟前").toString();
        } else if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600 + "小时前").toString();
        } else if (time >= 3600 * 24) {
            return sb.append((int) Math.floor(time / (3600 * 24)) + "天前").toString();
        }
        return "时间获取中";
    }

    /**
     * 日期字符串转换为Date
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static Date strToDate(String dateStr, String format) {
        Date date = null;

        if (!TextUtils.isEmpty(dateStr)) {
            DateFormat df = new SimpleDateFormat(format);
            try {
                date = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    /**
     * 日期转换为字符串
     *
     * @param timeStr
     * @param format
     * @return
     */
    public static String dateToString(String timeStr, String format) {
        // 判断是否是今年
        Date date = strToDate(timeStr, format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 如果是今年的话，才去“xx月xx日”日期格式
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            return DATE_FORMAT_TILL_DAY_CURRENT_YEAR.format(date);
        }

        return DATE_FORMAT_TILL_DAY_CH.format(date);
    }

    // 获取去年的年份
    public static int getLastYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return c.get(Calendar.YEAR);
    }

    // 获取下一年年份
    public static int getNextYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        return c.get(Calendar.YEAR);
    }

    public static String formatDate(int year, int month, int day) {//月份从0开始
        if (month < 10 && day < 10) {
            return year + "-" + "0" + (month + 1) + "-" + "0" + day;
        } else if (month < 10) {
            return year + "-" + "0" + (month + 1) + "-" + day;
        } else if (day < 10) {
            return year + "-" + (month + 1) + "-" + "0" + day;
        } else {
            return year + "-" + (month + 1) + "-" + day;
        }
    }

    public static String formatTime(int hour, int min) {
        if (hour < 10 && min < 10) {
            return "0" + hour + ":" + "0" + min;
        } else if (hour < 10) {
            return "0" + hour + ":" + min;
        } else if (min < 10) {
            return hour + ":" + "0" + min;
        } else {
            return hour + ":" + min;
        }
    }

    /**
     * 判断是否是闰年
     */
    public static boolean isRunNian(int year) {
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 00:00:00格式
     *
     * @param time
     * @return
     */
    public static String formateTimer(long time) {
        String str = "00:00";
        int hour = 0;
        if (time >= 1000 * 3600) {
            hour = (int) (time / (1000 * 3600));
            time -= hour * 1000 * 3600;
        }
        int minute = 0;
        if (time >= 1000 * 60) {
            minute = (int) (time / (1000 * 60));
            time -= minute * 1000 * 60;
        }
        int sec = (int) (time / 1000);
        str = formateNumber(minute) + ":" + formateNumber(sec);
        return str;
    }

    public static int getTimeFromStr(String timeStr) {
        int time = 0;
        String[] temp = timeStr.split(":");

        if (temp.length == 3) {
            time += Integer.valueOf(temp[0]) * 3600;
            time += Integer.valueOf(temp[1]) * 60;
            time += Integer.valueOf(temp[2]);
        } else {
            time += Integer.valueOf(temp[0]) * 60;
            time += Integer.valueOf(temp[1]);
        }
        return time;
    }

    /**
     * 00:00格式
     *
     * @param time
     * @return
     */
    public static String formateTimerMinAndSec(long time) {
        String str = "00:00";
        int minute = 0;
        if (time >= 1000 * 60) {
            minute = (int) (time / (1000 * 60));
            time -= minute * 1000 * 60;
        }
        int sec = (int) (time / 1000);
        str = formateNumber(minute) + ":" + formateNumber(sec);
        return str;
    }

    /**
     * 个位数前面自动加0
     *
     * @param time
     * @return
     */
    private static String formateNumber(int time) {
        return String.format("%02d", time);
    }

}
