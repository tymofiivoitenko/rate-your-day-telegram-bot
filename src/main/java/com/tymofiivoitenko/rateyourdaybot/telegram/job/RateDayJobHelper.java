package com.tymofiivoitenko.rateyourdaybot.telegram.job;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.RateSettings;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.service.RateSettingsService;
import com.tymofiivoitenko.rateyourdaybot.telegram.Bot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static com.tymofiivoitenko.rateyourdaybot.telegram.handler.RateDayHandler.RATE_DAY_KEY;
import static com.tymofiivoitenko.rateyourdaybot.telegram.handler.RateDayHandler.SCORE_KEY;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.SYSTEM_ZONE_ID;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createInlineKeyboardButton;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createMessageTemplate;

@Slf4j
@Component
@AllArgsConstructor
public class RateDayJobHelper {
    private static final String RATE_DAY_MESSAGE = "Як прошов твій день?";
    private static final String DATE_PATTERN_FORMAT = "yyyy-MM-dd";

    private final Bot bot;
    private final PersonService personService;
    private final RateSettingsService rateSettingsService;


    public void sendRateSurveys() {
        // TODO SAVE DATE IN UTC FORMAT
        var now = LocalDateTime.now();
        var currentHour = ZonedDateTime.of(now, SYSTEM_ZONE_ID).getHour();
        var currentAskTime = LocalTime.of(currentHour, 0);
        var personIds = this.rateSettingsService.findAllByAskTime(currentAskTime).stream()
                .map(RateSettings::getPersonId)
                .toList();
        var persons = this.personService.findByIdIn(personIds);

        log.info("UTC Time: {}, currentAskTime: {}, persons : {}", now, currentAskTime, personIds);

        persons.stream()
                .map(person -> createRateKeyBoardMessage(person, now))
                .forEach(message -> this.bot.executeWithExceptionCheck(message));
    }

    public static SendMessage createRateKeyBoardMessage(Person person, LocalDateTime now) {
        var zonedDate = DateTimeFormatter.ofPattern(DATE_PATTERN_FORMAT)
                .withZone(SYSTEM_ZONE_ID)
                .format(now);

        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var inlineKeyboardButtonsRow = IntStream.rangeClosed(1, 5).boxed()
                .map(String::valueOf)
                .map(score -> createInlineKeyboardButton(score, RATE_DAY_KEY + zonedDate + SCORE_KEY + score))
                .toList();

        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRow));
        var response = createMessageTemplate(person, RATE_DAY_MESSAGE);
        response.setReplyMarkup(inlineKeyboardMarkup);

        return response;
    }
}
