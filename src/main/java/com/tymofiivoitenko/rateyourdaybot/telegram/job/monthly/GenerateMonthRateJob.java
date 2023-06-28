package com.tymofiivoitenko.rateyourdaybot.telegram.job.monthly;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.SYSTEM_ZONE_ID;


@Slf4j
@Component
@AllArgsConstructor
public class GenerateMonthRateJob {

    private final GenerateMonthRateJobHelper helper;

    private final ScheduledExecutorService executorService;


    @Autowired
    public GenerateMonthRateJob(GenerateMonthRateJobHelper helper) {
        this.helper = helper;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executeNext();
    }

    private void executeNext() {
        Runnable task = () -> {
            helper.sendMonthRates();
            executeNext();
        };
        var delay = calculateDelay();

        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }

    private long calculateDelay() {
        var localNow = LocalDateTime.now();
        var zonedNow = ZonedDateTime.of(localNow, SYSTEM_ZONE_ID);
        var zonedNextTarget = zonedNow.plusMonths(1).withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        var delay = Duration.between(zonedNow, zonedNextTarget).getSeconds() + 1;

        log.info("Current time {}, send MonthRates in {} seconds", zonedNow, delay);
        return delay;
    }
}

