package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.RateSettings;
import com.tymofiivoitenko.rateyourdaybot.service.RateSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.deleteMessage;
import static org.apache.commons.lang3.StringUtils.substringAfter;


@Slf4j
@Component
@AllArgsConstructor
public class RateSettingsHandler implements Handler {

    public static final String RATE_SETTINGS = "/rate_settings_";
    public static final String RATING_SETTINGS_MESSAGE = "О котрій вас краще запитати про ваш день?";
    public static final String RATING_SETTINGS_SAVED_MESSAGE = "Домовились, до вечора)";

    private RateSettingsService rateSettingsService;

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String message, Integer messageId) {
        var askTime = substringAfter(message, RATE_SETTINGS).replace("-", ":");
        var rateSettings = this.rateSettingsService.findByPerson(person.getId())
                .orElse(new RateSettings());
        rateSettings.setAskTime(LocalTime.parse(askTime));
        rateSettings.setPersonId(person.getId());

        this.rateSettingsService.save(rateSettings);

        // TODO if time of asking if passed - ask right now

        return List.of(deleteMessage(person, messageId), createMessageTemplate(person, RATING_SETTINGS_SAVED_MESSAGE));
    }

    @Override
    public List<String> operatedActions() {
        return List.of(RATE_SETTINGS);
    }
}
