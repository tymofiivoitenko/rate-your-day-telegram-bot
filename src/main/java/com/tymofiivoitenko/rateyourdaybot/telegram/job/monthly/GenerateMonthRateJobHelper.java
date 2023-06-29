package com.tymofiivoitenko.rateyourdaybot.telegram.job.monthly;


import com.tymofiivoitenko.rateyourdaybot.model.calendar.CalendarScoreColour;
import com.tymofiivoitenko.rateyourdaybot.model.calendar.MonthRateView;
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

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
public class GenerateMonthRateJobHelper extends GenerateViewJobHelper {

    private static final String TEMPLATE_NAME = "rates-month-view.ftl";

    private static final String YEAR_MONTH_DATE_FORMAT = "yyyy-MM";

    private final PersonService personService;

    private final RateService rateService;

    private final Bot bot;

    private final Configuration freemarkerConfig;

    public void sendMonthRates() {
        var persons = this.personService.findAll();
        var month = ZonedDateTime.now(UKRAINE_ZONE_ID).toLocalDate().minusMonths(1).withDayOfMonth(1);

        for (Person person : persons) {
            try {
                sendMonthRate(person, month);
            } catch (Exception e) {
                log.error("Can't send month rate to person {}, due to ", person.getId(), e);
            }
        }
    }

    private void sendMonthRate(Person person, LocalDate month) {
        var yearAndMonth = DateTimeFormatter.ofPattern(YEAR_MONTH_DATE_FORMAT).format(month);
        var rates = this.rateService.getRatesByPersonIdAndMonth(person.getId(), yearAndMonth);

        try {
            var monthRateView = createMonthRateView(rates, month);
            var html = createByTemplate(monthRateView);
            var photoTemplate = createPhotoTemplate(person, generateInputStreamFromHtml(html));

            bot.execute(photoTemplate);
        } catch (IOException | TelegramApiException | TemplateException | ParserConfigurationException |
                 SAXException e) {
            log.error("Cannot generate month rate image due to ", e);
            throw new RuntimeException(e);
        }
    }

    private String createByTemplate(MonthRateView monthRateView) throws IOException, TemplateException {
        var template = this.freemarkerConfig.getTemplate(TEMPLATE_NAME, "UTF-8");
        var model = new HashMap<>() {{
            put("calendar", monthRateView);
        }};

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private MonthRateView createMonthRateView(List<Rate> rates, LocalDate month) {
        MonthRateView calendar = new MonthRateView();
        calendar.setMonthName(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        calendar.setYear(String.valueOf(month.getYear()));
        calendar.setRatesToDays(generateRatesToDays(month, rates));

        return calendar;
    }

    private List<List<Map.Entry<String, String>>> generateRatesToDays(LocalDate date, List<Rate> rates) {
        List<List<Map.Entry<String, String>>> ratesToDays = new ArrayList<>();
        var dayToColour = rates.stream()
                .collect(Collectors.toMap(it -> it.getDate().getDayOfMonth(), it -> CalendarScoreColour.valueOf(it.getScore())));
        var day = date.withDayOfMonth(1);
        int numberOfWeeksInMonth = (day.lengthOfMonth() / 7) + 1;

        for (int i = 1; i <= numberOfWeeksInMonth; i++) {
            var ratesByWeek = new ArrayList<Map.Entry<String, String>>();
            if (i == 1) {
                for (int j = 1; j < day.getDayOfWeek().getValue(); j++) {
                    ratesByWeek.add(Map.entry("", GREY.getHexCode()));
                }
            }

            for (int j = day.getDayOfWeek().getValue(); j <= 7; j++) {
                var colour = dayToColour.getOrDefault(day.getDayOfMonth(), GREY);
                ratesByWeek.add(Map.entry(String.valueOf(day.getDayOfMonth()), colour.getHexCode()));
                day = day.plusDays(1);
                if (day.getMonth() != date.getMonth()) {
                    break;
                }
            }

            if (i == numberOfWeeksInMonth) {
                for (int j = day.getDayOfWeek().getValue(); j <= 7; j++) {
                    ratesByWeek.add(Map.entry("", GREY.getHexCode()));
                }
            }
            ratesToDays.add(ratesByWeek);
        }

        return ratesToDays;
    }

}
