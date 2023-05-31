package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import com.tymofiivoitenko.rateyourdaybot.service.RateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.deleteMessage;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;


@Slf4j
@Component
@AllArgsConstructor
public class RateDayHandler implements Handler {

    public static final String RECEIVED_MESSAGE = "прийняв, зберіг. удачі вам по життю";
    public static final String RATE_DAY = "/rate_date_";
    public static final String SCORE = "_score_";


    private RateService rateService;

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String message, Integer messageId) {
        log.info("Message from person: " + message);

        if (message.startsWith(RATE_DAY)) {
            var date = substringBetween(message, RATE_DAY, SCORE);
            var score = substringAfter(message, SCORE);

            var rate = new Rate();
            rate.setDate(LocalDate.parse(date));
            rate.setScore(Integer.valueOf(score));
            rate.setPersonId(person.getId());

            this.rateService.save(rate);
            return List.of(deleteMessage(person, messageId), createMessageTemplate(person, RECEIVED_MESSAGE));
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> operatedActions() {
        return List.of("/rate_date");
    }
}
