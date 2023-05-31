package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.Serializable;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createInlineKeyboardButton;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;


@Slf4j
@Component
public class StartHandler implements Handler {

    private static final String WELCOME_MESSAGE = "Радий знайомству. Я призначений для того, щоб перепитувати як пройшов твій день і аналізувати.";

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String messageFromUser, Integer messageId) {
        var welcomeMessageTemplate = createMessageTemplate(person, WELCOME_MESSAGE);
        var rateSettingsTemplate = createRateSettingsTemplate(person);

        return List.of(welcomeMessageTemplate, rateSettingsTemplate);

    }

    private SendMessage createRateSettingsTemplate(Person person) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var inlineKeyboardButtonsRow = List.of(
                createInlineKeyboardButton("20:00", RateSettingsHandler.RATE_SETTINGS + "20-00"),
                createInlineKeyboardButton("21:00", RateSettingsHandler.RATE_SETTINGS + "21-00"),
                createInlineKeyboardButton("22:00", RateSettingsHandler.RATE_SETTINGS + "22-00"),
                createInlineKeyboardButton("23:00", RateSettingsHandler.RATE_SETTINGS + "23-00"));
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRow));
        var response = createMessageTemplate(person, RateSettingsHandler.RATING_SETTINGS_MESSAGE);
        response.setReplyMarkup(inlineKeyboardMarkup);

        return response;

    }

    @Override
    public List<String> operatedActions() {
        return List.of("/start");
    }
}
