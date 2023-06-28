package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import com.tymofiivoitenko.rateyourdaybot.service.RateService;
import com.tymofiivoitenko.rateyourdaybot.service.RateSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.telegram.handler.RateSettingsHandler.createRateSettingsTemplate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.deleteMessage;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;


@Slf4j
@Component
@AllArgsConstructor
public class RateDayHandler implements Handler {

    public static final String RECEIVED_MESSAGE = "Дякую, зберігла";
    public static final String RATE_DAY_KEY = "/rate_date_";
    public static final String SCORE_KEY = "_score_";

    private RateService rateService;

    private RateSettingsService rateSettingsService;

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String message, Integer messageId) {
        var messagesToSend = new ArrayList<BotApiMethod<? extends Serializable>>();
        var isFirstSurvey = this.rateService.isFirstRateSurvey(person.getId());
        var rateSettings = this.rateSettingsService.findByPerson(person.getId());
        var date = substringBetween(message, RATE_DAY_KEY, SCORE_KEY);
        var score = substringAfter(message, SCORE_KEY);

        var rate = new Rate();
        rate.setDate(LocalDate.parse(date));
        rate.setScore(Integer.valueOf(score));
        rate.setPersonId(person.getId());
        this.rateService.upsert(rate);

        messagesToSend.add(deleteMessage(person, messageId));
        if (isFirstSurvey) {
            messagesToSend.add(createMessageTemplate(person, RECEIVED_MESSAGE));
        }
        if (rateSettings.isEmpty()) {
            messagesToSend.add(createRateSettingsTemplate(person));
        }
        return messagesToSend;
    }

    @Override
    public List<String> operatedActions() {
        return List.of("/rate_date");
    }
}
