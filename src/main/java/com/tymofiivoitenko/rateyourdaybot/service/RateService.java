package com.tymofiivoitenko.rateyourdaybot.service;

import com.tymofiivoitenko.rateyourdaybot.dao.RateDao;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RateService {

    private final RateDao dao;

    public Rate save(Rate rate) {
        return this.dao.save(rate);
    }

}
