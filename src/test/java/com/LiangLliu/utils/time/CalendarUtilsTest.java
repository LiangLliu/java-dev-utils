package com.LiangLliu.utils.time;

import com.LiangLliu.utils.model.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.LiangLliu.utils.time.CalendarUtils.getCalendarMonths;
import static org.junit.jupiter.api.Assertions.*;

class CalendarUtilsTest {

    @Test
    public void should_match_when_validate_given_10_number_string() {
        LocalDate startDate = LocalDate.of(2020, 1, 23);
        LocalDate endDate = LocalDate.of(2021, 4, 5);


        List<Pair<LocalDate, LocalDate>> calendarMonthsStartAndEnd
                = getCalendarMonths(startDate, endDate);

        assertEquals("2020-01-23", calendarMonthsStartAndEnd.get(0).getFirst().toString());
        assertEquals("2020-01-31", calendarMonthsStartAndEnd.get(0).getSecond().toString());

        assertEquals("2020-02-01", calendarMonthsStartAndEnd.get(1).getFirst().toString());
        assertEquals("2020-02-29", calendarMonthsStartAndEnd.get(1).getSecond().toString());

    }
}
