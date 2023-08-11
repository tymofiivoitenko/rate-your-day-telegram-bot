package com.tymofiivoitenko.rateyourdaybot.util;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Component
public class DateUtil {

    public static LocalDate getDayOfWeek(int year, int weekNumber, DayOfWeek dayOfWeek) {
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear();
        return LocalDate.ofYearDay(year, 1)
                .with(weekOfYear, weekNumber)
                .with(dayOfWeek);
    }
}
