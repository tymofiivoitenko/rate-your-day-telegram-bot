package com.tymofiivoitenko.rateyourdaybot.dao;

import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface RateDao extends JpaRepository<Rate, Integer> {

    @Query(value = """
            SELECT *
            FROM rate
            WHERE person_id = :personId
              AND to_char(date, 'YYYY-MM') = :yearAndMonth
            """, nativeQuery = true
    )
    List<Rate> getRatesByPersonIdAndMonth(Integer personId, String yearAndMonth);

    @Query(value = """
            SELECT *
            FROM rate
            WHERE person_id = :personId
              AND date = :date
            """, nativeQuery = true
    )
    Optional<Rate> getRateByPersonIdAndDate(Integer personId, LocalDate date);

    @Query(value = """
             SELECT NOT EXISTS(SELECT 1
                           FROM rate
                           WHERe person_id = :personId)
            """, nativeQuery = true
    )
    boolean isFirstRateSurvey(Integer personId);
}
