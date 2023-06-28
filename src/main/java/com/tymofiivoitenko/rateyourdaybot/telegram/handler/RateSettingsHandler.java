package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.RateSettings;
import com.tymofiivoitenko.rateyourdaybot.service.RateSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createInlineKeyboardButton;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.deleteMessage;
import static org.apache.commons.lang3.StringUtils.substringAfter;


@Slf4j
@Component
@AllArgsConstructor
public class RateSettingsHandler implements Handler {

    public static final String RATE_SETTINGS = "/rate_settings_";
    public static final String RATING_SETTINGS_MESSAGE = "О котрій тобі писати?";
    public static final String RATING_SETTINGS_SAVED_MESSAGE = "Домовились, до вечора)";
    public static final List<String> SURVEY_TIME = List.of("20:00", "21:00", "22:00", "23:00");

    private RateSettingsService rateSettingsService;

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String message, Integer messageId) {
        var askTime = substringAfter(message, RATE_SETTINGS).replace("-", ":");
        var rateSettings = this.rateSettingsService.findByPerson(person.getId())
                .orElse(new RateSettings());
        rateSettings.setAskTime(LocalTime.parse(askTime));
        rateSettings.setPersonId(person.getId());

        this.rateSettingsService.save(rateSettings);

        return List.of(deleteMessage(person, messageId), createMessageTemplate(person, RATING_SETTINGS_SAVED_MESSAGE));
    }

    public static SendMessage createRateSettingsTemplate(Person person) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var inlineKeyboardButtonsRow = SURVEY_TIME.stream()
                .map(it -> createInlineKeyboardButton(it, RateSettingsHandler.RATE_SETTINGS + it.replace(':','-')))
                .toList();
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRow));
        var response = createMessageTemplate(person, RateSettingsHandler.RATING_SETTINGS_MESSAGE);
        response.setReplyMarkup(inlineKeyboardMarkup);

        return response;

    }

    @Override
    public List<String> operatedActions() {
        return List.of(RATE_SETTINGS);
    }
}
