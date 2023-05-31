package com.tymofiivoitenko.rateyourdaybot.telegram.job;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RateDayJob {

    public static final int UKRAINE_OFFSET = 3;

    public static final ZoneId SYSTEM_ZONE_ID = ZoneOffset.ofHours(UKRAINE_OFFSET);

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

    public void executeNext() {
        Runnable task = () -> {
            helper.sendMessages();
            executeNext();
        };
        var delay = calculateDelay();

        this.executorService.schedule(task, delay, TimeUnit.SECONDS);
    }

    private long calculateDelay() {
        var localNow = LocalDateTime.now();
        var zonedNow = ZonedDateTime.of(localNow, SYSTEM_ZONE_ID);
        var zonedNextTarget = zonedNow.plusHours(1).withMinute(0).withSecond(0).withNano(0);

        var duration = Duration.between(zonedNow, zonedNextTarget);
        log.info("Ukrainian Time: {}, zonedNextTarget {}, run next in {} seconds}", zonedNow, zonedNextTarget, duration);

        return duration.getSeconds();
    }
}
