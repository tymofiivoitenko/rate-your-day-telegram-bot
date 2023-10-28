package com.tymofiivoitenko.rateyourdaybot.dao;

import com.tymofiivoitenko.rateyourdaybot.model.rate.RateSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface RateSettingsDao extends JpaRepository<RateSettings, Integer> {

    Optional<RateSettings> findByPersonId(Integer personId);

    List<RateSettings> findAllByAskTime(LocalTime askTime);

}
