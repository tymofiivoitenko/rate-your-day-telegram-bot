package com.tymofiivoitenko.rateyourdaybot.telegram.job;


import com.tymofiivoitenko.rateyourdaybot.model.calendar.CalendarScoreColour;
import com.tymofiivoitenko.rateyourdaybot.model.calendar.MonthRateView;
import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.model.rate.Rate;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.service.RateService;
import com.tymofiivoitenko.rateyourdaybot.telegram.Bot;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tymofiivoitenko.rateyourdaybot.model.calendar.CalendarScoreColour.GREY;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.SYSTEM_ZONE_ID;
import static com.tymofiivoitenko.rateyourdaybot.util.TelegramUtil.createPhotoTemplate;


@Slf4j
@Component
@AllArgsConstructor
public class GenerateMonthRateJobHelper {

    private static final Integer CALENDAR_IMAGE_WIDTH = 2048;

    private static final Integer CALENDAR_IMAGE_HEIGHT = -1;

    private static final String CALENDAR_TEMPLATE_NAME = "calendar.ftl";

    private static final String YEAR_MONTH_DATE_FORMAT = "yyyy-MM";

    private final PersonService personService;

    private final RateService rateService;

    private final Bot bot;

    private final Configuration freemarkerConfig;

    public void sendMonthRates() {
        var persons = this.personService.findAll();
        var month = LocalDateTime.now().toLocalDate().minusMonths(1).withDayOfMonth(1);

        for (Person person : persons) {
            try {
                sendMonthRate(person, month);
            } catch (Exception e) {
                log.error("Can't send month rate to person {}, due to ", person.getId(), e);
            }
        }
    }

    public void sendMonthRate(Person person, LocalDate month) {
        var yearAndMonth = DateTimeFormatter.ofPattern(YEAR_MONTH_DATE_FORMAT)
                .withZone(SYSTEM_ZONE_ID)
                .format(month);
        var rates = this.rateService.getRatesByPersonIdAndMonth(person.getId(), yearAndMonth);

        try {
            var monthRateView = createMonthRateView(rates, month);
            var html = createByTemplate(monthRateView);
            var photoTemplate = createPhotoTemplate(person, generateInputStream(html));

            bot.execute(photoTemplate);
        } catch (IOException | TelegramApiException | TemplateException | ParserConfigurationException |
                 SAXException e) {
            log.error("Cannot generate month rate image due to ", e);
            throw new RuntimeException(e);
        }
    }

    private String createByTemplate(MonthRateView monthRateView) throws IOException, TemplateException {
        var template = this.freemarkerConfig.getTemplate(CALENDAR_TEMPLATE_NAME, "UTF-8");
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

    private InputStream generateInputStream(String html) throws ParserConfigurationException, IOException, SAXException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(html.getBytes()));

        Java2DRenderer imageRenderer = new Java2DRenderer(doc, CALENDAR_IMAGE_WIDTH, CALENDAR_IMAGE_HEIGHT);
        imageRenderer.setBufferedImageType(BufferedImage.TYPE_INT_RGB);

        BufferedImage image = imageRenderer.getImage();
        var os = new ByteArrayOutputStream();

        ImageIO.write(image, "jpeg", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

}
