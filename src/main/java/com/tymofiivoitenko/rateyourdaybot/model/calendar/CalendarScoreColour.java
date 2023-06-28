package com.tymofiivoitenko.rateyourdaybot.model.calendar;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CalendarScoreColour {
    GREY("#dcdcdc", null),
    RED("#ff5a24", 1),
    ORANGE("orange", 2),
    YELLOW("#f6f667", 3),
    CHARTREUSE("#B7FF00", 4),
    GREEN("#4ED93A", 5);

    String hexCode;
    Integer score;

    CalendarScoreColour(String hexCode, Integer score) {
        this.hexCode = hexCode;
        this.score = score;
    }

    public static CalendarScoreColour valueOf(Integer score) {
        return Arrays.stream(CalendarScoreColour.values())
                .filter(e -> e.getScore() == score)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Colour for score " + score + " does not exist"));
    }
}
