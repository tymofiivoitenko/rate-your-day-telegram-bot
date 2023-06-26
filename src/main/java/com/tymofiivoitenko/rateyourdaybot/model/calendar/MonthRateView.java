package com.tymofiivoitenko.rateyourdaybot.model.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MonthRateView {

    private String monthName;

    private String year;

    private List<List<Map.Entry<String, String>>> ratesToDays;
}
