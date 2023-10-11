package com.tymofiivoitenko.rateyourdaybot.telegram.job.monthly;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tymofiivoitenko.rateyourdaybot.util.DateUtil.calculateDelayTillNextMonthRate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;


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
        var now = ZonedDateTime.now(UKRAINE_ZONE_ID);
        var delay = calculateDelayTillNextMonthRate(now);

        log.info("Current zoned time {}, sending MonthRates in {} seconds. ", now, delay);
        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }
}

