package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.telegram.handler.RateSettingsHandler.createRateSettingsTemplate;
import static com.tymofiivoitenko.rateyourdaybot.telegram.job.daily.RateDayJobHelper.createRateKeyBoardMessage;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;


@Slf4j
@Component
public class StartHandler implements Handler {

    private static final String WELCOME_MESSAGE_MORNING_HOURS = "Рада познайомитись. Росскажеш мені як пройшов твій день? Хоча, краще вже ввечері перепитаю.";
    private static final String WELCOME_MESSAGE_LATE_NIGHT_HOURS = "Рада познайомитись. Вже так піздно..";

    @Override
    public List<BotApiMethod<? extends Serializable>> handle(Person person, String messageFromUser, Integer messageId) {
        var messagesToSend = new ArrayList<BotApiMethod<? extends Serializable>>();
        var currentHour = ZonedDateTime.now(UKRAINE_ZONE_ID).getHour();

        if (currentHour < 20) {
            messagesToSend.add(createMessageTemplate(person, WELCOME_MESSAGE_MORNING_HOURS));
            messagesToSend.add(createRateSettingsTemplate(person));
        } else {
            messagesToSend.add(createMessageTemplate(person, WELCOME_MESSAGE_LATE_NIGHT_HOURS));
            messagesToSend.add(createRateKeyBoardMessage(person, ZonedDateTime.now(UKRAINE_ZONE_ID).toLocalDate()));
        }

        return messagesToSend;

    }

    @Override
    public List<String> operatedActions() {
        return List.of("/start");
    }
}
