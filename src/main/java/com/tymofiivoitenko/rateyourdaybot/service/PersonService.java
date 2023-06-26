package com.tymofiivoitenko.rateyourdaybot.service;

import com.tymofiivoitenko.rateyourdaybot.dao.PersonDao;
import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@AllArgsConstructor
@Service
public class PersonService {

    private final PersonDao dao;

    public Person getOrCreate(Message message) {
        var chatId = message.getFrom().getId();

        return getOrCreate(chatId, message.getFrom());
    }

    public Person getOrCreate(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();

        return getOrCreate(chatId, callbackQuery.getFrom());
    }

    public List<Person> findByIdIn(List<Integer> ids) {
        return this.dao.findByIdIn(ids);
    }

    private Person getOrCreate(Long chatId, User user) {
        return this.dao.getByChatId(chatId)
                .orElseGet(() -> {
                    var person = new Person(chatId, user.getFirstName(), user.getLastName(), user.getUserName());
                    return this.dao.save(person);
                });
    }
}
