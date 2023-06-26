package com.tymofiivoitenko.rateyourdaybot.telegram;

import com.tymofiivoitenko.rateyourdaybot.telegram.updateReceiver.UpdateReceiver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
@Getter
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.username}")
    private String botUsername;

    private final UpdateReceiver updateReceiver;

    public Bot(UpdateReceiver updateReceiver) {
        this.updateReceiver = updateReceiver;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var messagesToSend = this.updateReceiver.handle(update);

        if (messagesToSend != null && !messagesToSend.isEmpty()) {
            messagesToSend
                    .forEach(m -> executeWithExceptionCheck(m));
        }
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T executeWithExceptionCheck(Method method) {
        try {
            return execute(method);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
