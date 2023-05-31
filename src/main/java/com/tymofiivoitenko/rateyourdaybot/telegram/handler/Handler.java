package com.tymofiivoitenko.rateyourdaybot.telegram.handler;


import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {

    List<BotApiMethod<? extends Serializable>> handle(Person person, String messageFromUser, Integer messageId);

    List<String> operatedActions();
}
