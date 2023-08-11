package com.tymofiivoitenko.rateyourdaybot.telegram.job.weekly;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;


@Slf4j
@Component
@AllArgsConstructor
public class GenerateWeekRateJob {

    private final GenerateWeekRateJobHelper helper;

    private final ScheduledExecutorService executorService;


    @Autowired
    public GenerateWeekRateJob(GenerateWeekRateJobHelper helper) {
        this.helper = helper;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executeNext();
    }

    private void executeNext() {
        Runnable task = () -> {
            helper.sendWeekRateViews();
            executeNext();
        };
        var delay = calculateDelay();

        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }

    // TODO FIX - calculate closest time, instead of next monday
    private long calculateDelay() {
        var zonedNow = ZonedDateTime.now(UKRAINE_ZONE_ID);
        var zonedNextTarget = zonedNow.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).withHour(9).withMinute(0).withSecond(1);
        var delay = Duration.between(zonedNow, zonedNextTarget).getSeconds() + 1;

        log.info("Current zoned time {}, send WeekRates in {} seconds", zonedNow, delay);
        return delay;
    }
}

