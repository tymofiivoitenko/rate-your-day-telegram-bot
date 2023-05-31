package com.tymofiivoitenko.rateyourdaybot.dao;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PersonDao extends JpaRepository<Person, Integer> {

    Optional<Person> getByChatId(Long chatId);

    List<Person> findByIdIn(List<Integer> ids);
}
