package com.tymofiivoitenko.rateyourdaybot.telegram.job.weekly;


import com.tymofiivoitenko.rateyourdaybot.model.calendar.CalendarScoreColour;
import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.service.RateService;
import com.tymofiivoitenko.rateyourdaybot.telegram.Bot;
import com.tymofiivoitenko.rateyourdaybot.telegram.job.GenerateViewJobHelper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tymofiivoitenko.rateyourdaybot.model.calendar.CalendarScoreColour.GREY;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.UKRAINE_ZONE_ID;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createPhotoTemplate;


@Slf4j
@Component
@AllArgsConstructor
public class GenerateWeekRateJobHelper extends GenerateViewJobHelper {

    private static final String TEMPLATE_NAME = "rates-week-view.ftl";

    private static final TemporalField weekOfWeekBasedYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

    private final PersonService personService;

    private final RateService rateService;

    private final Bot bot;

    private final Configuration freemarkerConfig;

//    @PostConstruct
//    private void init() {
//        var person = this.personService.findByIdIn(List.of(1)).get(0);
//        sendWeekRateView(person, ZonedDateTime.now(UKRAINE_ZONE_ID).with(TemporalAdjusters.next(DayOfWeek.MONDAY)).toLocalDate().minusWeeks(1));
//    }

    public void sendWeekRateViews() {
        var persons = this.personService.findAll();
        var week = ZonedDateTime.now(UKRAINE_ZONE_ID).toLocalDate().minusWeeks(1);
        for (Person person : persons) {
            try {
                sendWeekRateView(person, week);
            } catch (Exception e) {
                log.error("Can't send week rate to person {}, due to ", person.getId(), e);
            }
        }
    }

    public void sendWeekRateView(Person person, LocalDate week) {
        if (week.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalStateException("First day of provided week is not a Monday");
        }
        var rates = this.rateService.getRatesByPersonIdBetweenDates(person.getId(), week, week.plusDays(6));

        try {
            var ratesToDays = generateRatesToDays(rates, week);
            var html = createByTemplate(ratesToDays, week);
            var photoTemplate = createPhotoTemplate(person, generateInputStreamFromHtml(html));

            bot.execute(photoTemplate);
        } catch (IOException | TelegramApiException | TemplateException | ParserConfigurationException |
                 SAXException e) {
            log.error("Cannot generate week rate image due to ", e);
            throw new RuntimeException(e);
        }
    }

    private String createByTemplate(List<Map.Entry<String, String>> ratesToDays, LocalDate week) throws IOException, TemplateException {
        var template = this.freemarkerConfig.getTemplate(TEMPLATE_NAME, "UTF-8");
        var model = new HashMap<>() {{
            put("weekNumber", week.get(weekOfWeekBasedYear));
            put("view", ratesToDays);
        }};

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    // TODO: Refactor
    private List<Map.Entry<String, String>> generateRatesToDays(List<Rate> rates, LocalDate date) {
        List<Map.Entry<String, String>> ratesToDays = new ArrayList<>();
        var dayToColour = rates.stream()
                .collect(Collectors.toMap(it -> it.getDate().getDayOfMonth(), it -> CalendarScoreColour.valueOf(it.getScore())));
        var day = date;
        for (int i = day.getDayOfWeek().getValue(); i <= 7; i++) {
            var colour = dayToColour.getOrDefault(day.getDayOfMonth(), GREY);
            ratesToDays.add(Map.entry(String.valueOf(day.getDayOfMonth()), colour.getHexCode()));
            day = day.plusDays(1);
        }

        return ratesToDays;
    }
}
