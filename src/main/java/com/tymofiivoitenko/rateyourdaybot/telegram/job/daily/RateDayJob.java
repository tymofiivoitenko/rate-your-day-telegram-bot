package com.tymofiivoitenko.rateyourdaybot.telegram.job.daily;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tymofiivoitenko.rateyourdaybot.util.DateUtil.calculateDelayTillNextRateDay;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;

@Slf4j
@Component
public class RateDayJob {

    private final RateDayJobHelper helper;

    private final ScheduledExecutorService executorService;

    public RateDayJob(RateDayJobHelper helper) {
        this.helper = helper;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executeNext();
    }

    private void executeNext() {
        Runnable task = () -> {
            helper.sendRateSurveys();
            executeNext();
        };
        var zonedNow = ZonedDateTime.now(UKRAINE_ZONE_ID);
        var delay = calculateDelayTillNextRateDay(zonedNow);

        log.info("Current zoned time: {}. Send RateSurveys in {} seconds", zonedNow, delay);

        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }
}
