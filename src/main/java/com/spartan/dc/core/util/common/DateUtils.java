package com.spartan.dc.core.util.common;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Time tool class
 *
 * @author rjx
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";



    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * Get the current date with "Date" type
     *
     * @return Date() Current date
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * Get the current date, the default format is: yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Date path, i.e. year/month/day, for example: 2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * Date path, i.e. year/month/day, for example: 20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * Date string to date format
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }


    public static String formatDate(String format, Date date) {
        return parseDateToStr(format, date);
    }


    /**
     * Get the server start time
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * Calculate the difference between two times
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // Get the time difference by millisecond between two times
        long diff = endDate.getTime() - nowDate.getTime();
        // Calculate the difference in days
        long day = diff / nd;
        // Calculate the difference in hours
        long hour = diff % nd / nh;
        // Calculate the difference in minutes
        long min = diff % nd % nh / nm;
        // Calculate the difference in seconds  // output result
        // long sec = diff % nd % nh % nm / ns;
        return day + "day" + hour + "hour" + min + "min";
    }

    /**
      * Date format Aug 23, 2022  14:41:23
      *
      **/
    public static String getMonthYearDate(Date date) {
        String months[] = {
                "Jan", "Feb", "Mar", "Apr",
                "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec"};

        // Initialize Gregorian calendar
        GregorianCalendar gcalendar = new GregorianCalendar();
        gcalendar.setTime(date);

        return months[gcalendar.get(Calendar.MONTH)]
                + " " + gcalendar.get(Calendar.DATE) + "，"
                + gcalendar.get(Calendar.YEAR)
                + "  " + gcalendar.get(Calendar.HOUR_OF_DAY) + ":"
                + gcalendar.get(Calendar.MINUTE) + ":"
                + (gcalendar.get(Calendar.SECOND) == 0 ? "00" : gcalendar.get(Calendar.SECOND));
    }

}
