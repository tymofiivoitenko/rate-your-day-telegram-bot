package com.tymofiivoitenko.rateyourdaybot.util;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TelegramUtil {

    public static final int UKRAINE_UTC_OFFSET = 3;

    public static final ZoneId SYSTEM_ZONE_ID = ZoneOffset.ofHours(UKRAINE_UTC_OFFSET);

    public static DeleteMessage deleteMessage(Person person, Integer messageId) {
        return DeleteMessage.builder()
                .chatId(person.getChatId().toString())
                .messageId(messageId)
                .build();
    }

    public static SendMessage createMessageTemplate(Person person, String messageToSend) {
        return SendMessage.builder()
                .chatId(person.getChatId().toString())
                .text(messageToSend)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public static SendPhoto createPhotoTemplate(Person person, InputStream is) {
        return SendPhoto.builder()
                .chatId(person.getChatId().toString())
                .parseMode(ParseMode.MARKDOWN)
                .photo(new InputFile(is, "calendar.jpeg"))
                .build();
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(command)
                .build();
    }
}
