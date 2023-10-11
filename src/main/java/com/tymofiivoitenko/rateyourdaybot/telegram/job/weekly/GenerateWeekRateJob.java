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

import static com.tymofiivoitenko.rateyourdaybot.util.DateUtil.calculateDelayTillNextWeekRate;
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
        var now = ZonedDateTime.now(UKRAINE_ZONE_ID);
        var delay = calculateDelayTillNextWeekRate(now);

        log.info("Current zoned time {}, send WeekRates in {} seconds", now, delay);

        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }
}

