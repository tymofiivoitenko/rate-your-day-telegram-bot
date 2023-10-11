package com.tymofiivoitenko.rateyourdaybot.controller;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.telegram.job.monthly.GenerateMonthRateJobHelper;
import com.tymofiivoitenko.rateyourdaybot.telegram.job.weekly.GenerateWeekRateJobHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.util.DateUtil.getDayOfWeek;

@Slf4j
@RestController
@RequestMapping("system")
@AllArgsConstructor
public class SystemController {

    private final PersonService personService;
    private final GenerateWeekRateJobHelper weekReportHelper;
    private final GenerateMonthRateJobHelper monthReportHelper;

    @PostMapping("/week-rate")
    private void sendWeekRates(@RequestParam Integer year,
                               @RequestParam Integer weekNumber,
                               @RequestParam List<Integer> personIds,
                               @RequestParam boolean dryRun) {
        log.info("Send week rate to persons {}, year {}, weekNumber {}, dryRun - {}", personIds, year, weekNumber, dryRun);
        var persons = personService.findByIdIn(personIds);

        for (Person person : persons) {
            try {
                var mondayInNeededWeek = getDayOfWeek(year, weekNumber, DayOfWeek.MONDAY);
                log.info("Sending week rate views # {} to person {}", mondayInNeededWeek, person.getId());

                if (!dryRun) {
                    this.weekReportHelper.sendWeekRateView(person, mondayInNeededWeek);
                }
                log.info("Week rate #{} view has been sent to person {}", mondayInNeededWeek, person.getId());
            } catch (Exception e) {
                log.error("Failed to send week rate view to person {}, due to {} ", person.getId(), e);
            }
        }
    }

    @PostMapping("/month-rate")
    private void sendMonthRates(@RequestParam Integer year,
                                @RequestParam Integer monthNumber,
                                @RequestParam List<Integer> personIds,
                                @RequestParam boolean dryRun) {
        log.info("Send month rate to persons {}, year {}, monthNumber {}, dryRun - {}", personIds, year, monthNumber, dryRun);
        var persons = personService.findByIdIn(personIds);

        for (Person person : persons) {
            try {
                var month = LocalDate.of(year, monthNumber, 1);
                log.info("Sending week rate views from {} to person {}", month, person.getId());

                if (!dryRun) {
                    this.monthReportHelper.sendMonthRate(person, month);
                }
                log.info("Month rate view has been sent", month, person.getId());
            } catch (Exception e) {
                log.error("Failed to send month rate view to person {}, due to {} ", person.getId(), e);
            }
        }
    }
}
