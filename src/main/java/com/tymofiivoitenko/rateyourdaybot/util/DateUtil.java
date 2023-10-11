package com.tymofiivoitenko.rateyourdaybot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Slf4j
@Component
public class DateUtil {

    public static LocalDate getDayOfWeek(int year, int weekNumber, DayOfWeek dayOfWeek) {
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear();
        return LocalDate.ofYearDay(year, 1)
                .with(weekOfYear, weekNumber)
                .with(dayOfWeek);
    }

    public static long calculateDelayTillNextRateDay(ZonedDateTime zonedNow) {
        var zonedNextTarget = zonedNow.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        log.info("Daily Rate Target: " + zonedNextTarget);

        return Duration.between(zonedNow, zonedNextTarget).getSeconds() + 1;
    }

    // TODO FIX - calculate closest time, instead of next monday
    public static long calculateDelayTillNextWeekRate(ZonedDateTime zonedNow) {
        var zonedNextTarget = zonedNow.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(9).withMinute(0).withSecond(1);
        log.info("Week Rate Target: " + zonedNextTarget);

        return Duration.between(zonedNow, zonedNextTarget).getSeconds() + 1;
    }

    public static long calculateDelayTillNextMonthRate(ZonedDateTime zonedNow) {
        var zonedNowPossibleSendingTime = zonedNow.withHour(11).withMinute(0).withSecond(0).withNano(0);
        var plusMonths = 1;
        if (zonedNow.getDayOfMonth() == 1 && zonedNow.isBefore(zonedNowPossibleSendingTime)) {
            plusMonths = 0;
        }
        var zonedNextTarget = zonedNow.plusMonths(plusMonths).withDayOfMonth(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
        log.info("Month Rate Target: " + zonedNextTarget);

        return Duration.between(zonedNow, zonedNextTarget).getSeconds() + 1;
    }
}
