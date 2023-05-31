package com.tymofiivoitenko.rateyourdaybot.dao;

import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RateDao extends JpaRepository<Rate, Integer> {

}
