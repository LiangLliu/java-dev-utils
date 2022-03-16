package com.LiangLliu.utils.time;


import com.LiangLliu.utils.model.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {

    /**
     * ISO8061 标准，规定，每一年的第一周如果大于 4天，所在的周才会被算到这一年中
     */
    private static final int ISO8061_MINIMAL_DAYS_IN_FIRST_WEEK = 4;

    private static final WeekFields DEFAULT_WEEK_FIELDS
            = WeekFields.of(DayOfWeek.SUNDAY, ISO8061_MINIMAL_DAYS_IN_FIRST_WEEK);

    /**
     * 获取 LocalData 所在的周
     */
    public static int getWeekWithLocalDate(LocalDate localDate) {
        return localDate.get(DEFAULT_WEEK_FIELDS.weekOfWeekBasedYear());
    }


    /**
     * 获取 获取LocalDate的周日
     */
    public static LocalDate getSundayWithLocalDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }


    /**
     * 获取 获取LocalDate的周六
     */
    public static LocalDate getSaturdayWithLocalDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    /**
     * 获取 LocalDate 月的第一天
     */
    public static LocalDate getFirstDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }


    /**
     * 获取 LocalDate 月的最后一天
     */
    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }


    /**
     * 获取任意时间范围的 CalendarWeeks, 返回一个 List， 每个元素包含每周的开始时间和结束时间
     * <p>
     * 参数：
     * 开始日期： 类型; LocalDate
     * 截止日期： 类型; LocalDate
     *
     * @Return : List<Pair<LocalDate, LocalDate>>
     */
    public static List<Pair<LocalDate, LocalDate>> getCalendarWeeks(LocalDate startDate,
                                                                    LocalDate endDate) {

        LocalDate startWithSunday = getSundayWithLocalDate(startDate);
        LocalDate endWithSaturday = getSaturdayWithLocalDate(endDate);

        List<Pair<LocalDate, LocalDate>> list = new ArrayList<>();

        while (startWithSunday.isBefore(endWithSaturday)) {
            LocalDate saturdayWithLocalDate = getSaturdayWithLocalDate(startWithSunday);

            Pair<LocalDate, LocalDate> of
                    = Pair.of(startWithSunday, saturdayWithLocalDate);

            list.add(of);

            startWithSunday = startWithSunday.plusWeeks(1);
        }

        setStartDateAndEndDate(startDate, endDate, list);
        return list;
    }


    private static void setStartDateAndEndDate(LocalDate startDate,
                                               LocalDate endDate,
                                               List<Pair<LocalDate, LocalDate>> calendarMonths) {
        if (calendarMonths.size() > 0) {
            calendarMonths.set(0, Pair.of(startDate, calendarMonths.get(0).getSecond()));

            int lastIndex = calendarMonths.size() - 1;
            calendarMonths.set(lastIndex, Pair.of(calendarMonths.get(lastIndex).getFirst(), endDate));
        }
    }


    /**
     * 获取任意时间范围的 CalendarMonth, 返回一个 List， 每个元素包含每月的开始时间和结束时间
     * <p>
     * 参数：
     * 开始日期： 类型; LocalDate
     * 截止日期： 类型; LocalDate
     *
     * @Return : List<Pair<LocalDate, LocalDate>>
     */
    public static List<Pair<LocalDate, LocalDate>> getCalendarMonths(LocalDate startDate,
                                                                     LocalDate endDate) {

        LocalDate startWithFirstDayOfMonth = getFirstDayOfMonth(startDate);
        LocalDate endWithLastDayOfMonth = getLastDayOfMonth(endDate);

        List<Pair<LocalDate, LocalDate>> list = new ArrayList<>();

        while (startWithFirstDayOfMonth.isBefore(endWithLastDayOfMonth)) {
            LocalDate lastDayOfMonth = startWithFirstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            Pair<LocalDate, LocalDate> localDatePair = Pair.of(startWithFirstDayOfMonth, lastDayOfMonth);

            list.add(localDatePair);

            startWithFirstDayOfMonth = startWithFirstDayOfMonth.plusMonths(1);
        }
        setStartDateAndEndDate(startDate, endDate, list);

        return list;
    }

}
