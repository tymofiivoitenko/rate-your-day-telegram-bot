package com.tymofiivoitenko.rateyourdaybot;


import com.tymofiivoitenko.rateyourdaybot.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DateUtilTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    DateUtil dateUtil;

    @Test
    void calculateDelayTillNextMonthRate() {
        var now = LocalDateTime.of(2023, 9, 15, 0, 0, 0);
        var zonedNow = ZonedDateTime.of(now, UKRAINE_ZONE_ID);
        var delay = this.dateUtil.calculateDelayTillNextMonthRate(zonedNow);

        assertEquals(delay, 1422001); // 16 days 11 hours
    }

    @Test
    void calculateDelayTillNextMonthRate_the() {
        var now = LocalDateTime.of(2023, 10, 1, 4, 0, 0); // early morning, the day of sending month rate
        var zonedNow = ZonedDateTime.of(now, UKRAINE_ZONE_ID);
        var delay = this.dateUtil.calculateDelayTillNextMonthRate(zonedNow);

        assertEquals(delay, 25201); // 7 hours
    }
}
