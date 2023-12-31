package com.tymofiivoitenko.rateyourdaybot.service;

import com.tymofiivoitenko.rateyourdaybot.dao.RateDao;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class RateService {

    private final RateDao dao;

    // UPSERT while BETA
    public Rate upsert(Rate rate) {
        var rateToSave = this.dao.getRateByPersonIdAndDate(rate.getPersonId(), rate.getDate())
                .map(existingRate -> {
                    existingRate.setScore(rate.getScore());
                    return existingRate;
                })
                .orElse(rate);

        return this.dao.save(rateToSave);
    }

    public List<Rate> getRatesByPersonIdAndMonth(Integer personId, String yearAndMonth) {
        return this.dao.getRatesByPersonIdAndMonth(personId, yearAndMonth);
    }

    public List<Rate> getRatesByPersonIdBetweenDates(Integer personId, LocalDate startDate, LocalDate endDate) {
        return this.dao.getRatesByPersonIdBetweenDates(personId, startDate, endDate);
    }

    public boolean isFirstRateSurvey(Integer personId) {
        return !this.dao.existsByPersonId(personId);
    }

    public List<Integer> getPersonIdWithRateByDate(List<Integer> personIds, LocalDate date) {
        return this.dao.getPersonIdWithRateByDate(personIds, date);
    }

}
