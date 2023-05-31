package com.tymofiivoitenko.rateyourdaybot.telegram.updateReceiver;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.type.UpdateType;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.telegram.handler.Handler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class UpdateReceiver {

    private final List<Handler> handlers;

    private final PersonService personService;

    public List<BotApiMethod<? extends Serializable>> handle(Update update) {
        var person = getPerson(update);
        var action = getAction(update);
        var messageId = getMessageId(update);

        return getHandlerByAction(action).handle(person, action, messageId);
    }

    private Person getPerson(Update update) {
        var updateType = getType(update);

        return switch (updateType) {
            case MESSAGE -> this.personService.getOrCreate(update.getMessage());
            case CALLBACK_QUERY -> this.personService.getOrCreate(update.getCallbackQuery());
        };
    }


    private String getAction(Update update) {
        var updateType = getType(update);

        return switch (updateType) {
            case MESSAGE -> update.getMessage().getText();
            case CALLBACK_QUERY -> update.getCallbackQuery().getData();
        };
    }

    private Integer getMessageId(Update update) {
        var updateType = getType(update);

        return switch (updateType) {
            case MESSAGE -> update.getMessage().getMessageId();
            case CALLBACK_QUERY -> update.getCallbackQuery().getMessage().getMessageId();
        };
    }

    private Handler getHandlerByAction(String action) {
        return this.handlers.stream()
                .filter(h -> h.operatedActions().stream()
                        .anyMatch(action::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private UpdateType getType(Update update) {
        if (update.hasCallbackQuery()) {
            return UpdateType.CALLBACK_QUERY;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            return UpdateType.MESSAGE;
        }
        throw new UnsupportedOperationException();
    }

}
