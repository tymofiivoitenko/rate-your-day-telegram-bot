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
              AND DATE_FORMAT(date, '%Y-%m') = :yearAndMonth
            """, nativeQuery = true)
    List<Rate> getRatesByPersonIdAndMonth(Integer personId, String yearAndMonth);

    @Query(value = """
            SELECT *
            FROM rate
            WHERE person_id = :personId
              AND date >= :startDate
              AND date <= :endDate
            """, nativeQuery = true)
    List<Rate> getRatesByPersonIdBetweenDates(Integer personId, LocalDate startDate, LocalDate endDate);

    @Query(value = """
            SELECT *
            FROM rate
            WHERE person_id = :personId
              AND date = :date
            """, nativeQuery = true)
    Optional<Rate> getRateByPersonIdAndDate(Integer personId, LocalDate date);

    @Query(value = """
            SELECT person_id
            FROM rate
            WHERE person_id IN (:personIds)
              AND date = :date
            """, nativeQuery = true)
    List<Integer> getPersonIdWithRateByDate(List<Integer> personIds, LocalDate date);

    boolean existsByPersonId(Integer personId);
}
