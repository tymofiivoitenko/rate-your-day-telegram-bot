package com.tymofiivoitenko.rateyourdaybot.service;

import com.tymofiivoitenko.rateyourdaybot.dao.RateSettingsDao;
import com.tymofiivoitenko.rateyourdaybot.model.rate.RateSettings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RateSettingsService {

    private final RateSettingsDao dao;

    public RateSettings save(RateSettings rate) {
        return this.dao.save(rate);
    }

    public Optional<RateSettings> findByPerson(Integer personId) {
        return this.dao.findByPersonId(personId);
    }

    public List<RateSettings> findAllByAskTime(LocalTime askTime) {
        return this.dao.findAllByAskTime(askTime);
    }
}
