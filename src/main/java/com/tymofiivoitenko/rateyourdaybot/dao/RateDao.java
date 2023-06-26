package com.tymofiivoitenko.rateyourdaybot.dao;

import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


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

}
