package com.accounting.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具类
 * 提供日期格式化、解析等功能
 */
public class DateUtil {

    /** 日期时间格式 */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 日期格式 */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /** 月份格式 */
    public static final String MONTH_PATTERN = "yyyy-MM";

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern(MONTH_PATTERN);

    /**
     * 获取当前日期时间字符串
     * @return 日期时间字符串
     */
    public static String now() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期字符串
     * @return 日期字符串
     */
    public static String today() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 格式化日期时间
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化日期
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * 解析日期时间字符串
     * @param str 日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String str) {
        return LocalDateTime.parse(str, DATETIME_FORMATTER);
    }

    /**
     * 解析日期字符串
     * @param str 日期字符串
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String str) {
        return LocalDate.parse(str, DATE_FORMATTER);
    }

    /**
     * 获取指定日期所在周的开始日期（周一）
     * @param date 指定日期
     * @return 周开始日期
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取指定日期所在周的结束日期（周日）
     * @param date 指定日期
     * @return 周结束日期
     */
    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取指定日期所在月的开始日期
     * @param date 指定日期
     * @return 月开始日期
     */
    public static LocalDate getMonthStart(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取指定日期所在月的结束日期
     * @param date 指定日期
     * @return 月结束日期
     */
    public static LocalDate getMonthEnd(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 判断日期是否在范围内（包含边界）
     * @param date 待判断日期
     * @param start 开始日期
     * @param end 结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
