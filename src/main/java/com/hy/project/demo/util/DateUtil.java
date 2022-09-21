package com.hy.project.demo.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class DateUtil {

    public static final String STANDARD_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String NGINX_LOG_DATE = "dd/MMM/yyyy:HH:mm:ss Z";
    public static final String TODAY_STR = "yyyyMMdd";

    public static String formatToday() {
        return format(new Date(), TODAY_STR);
    }

    public static Date parse(String date, String format) {
        if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(Date date, String format) {
        if (null == date || StringUtils.isBlank(format)) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(long timestamp, String format) {
        Date date = new Date(timestamp);
        return format(date, format);
    }

    public static Date parseNginxDate(String dateStr) {
        if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat(NGINX_LOG_DATE, Locale.ENGLISH).parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getStartOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return calendar.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * 比较日期start与日期end之间差了多少天
     * 只计算天的维度，不计算小时/分钟/秒的差异
     *
     * @param start 开始日期
     * @param end 结束日期
     * @return 结果
     */
    public static long dayDiff(Date start, Date end) {
        return (getStartOfDate(end).getTime() - getStartOfDate(start).getTime()) / (1000 * 60 * 60 * 24);
    }

    public static Date addHours(Date date, int hours) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

}
