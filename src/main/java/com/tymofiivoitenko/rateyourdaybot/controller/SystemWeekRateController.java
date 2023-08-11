package com.tymofiivoitenko.rateyourdaybot.controller;

import com.tymofiivoitenko.rateyourdaybot.model.person.Person;
import com.tymofiivoitenko.rateyourdaybot.service.PersonService;
import com.tymofiivoitenko.rateyourdaybot.telegram.job.weekly.GenerateWeekRateJobHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.util.List;

import static com.tymofiivoitenko.rateyourdaybot.util.DateUtil.getDayOfWeek;

@Slf4j
@RestController("week-rate")
@AllArgsConstructor
public class SystemWeekRateController {

    private final PersonService personService;
    private final GenerateWeekRateJobHelper helper;

    @PostMapping("send")
    private void sendRates(@RequestParam Integer year,
                           @RequestParam Integer weekNumber,
                           @RequestParam List<Integer> personIds,
                           @RequestParam boolean dryRun) {
        log.info("Send rate to persons {}, year {}, weekNumber {}, dryRun - {}", personIds, year, weekNumber, dryRun);
        var persons = personService.findByIdIn(personIds);

        for (Person person : persons) {
            try {
                var mondayInNeededWeek = getDayOfWeek(year, weekNumber, DayOfWeek.MONDAY);
                log.info("Sending week rate views from {} to person {}", mondayInNeededWeek, person.getId());

                if (!dryRun) {
                    this.helper.sendWeekRateView(person, mondayInNeededWeek);
                }
                log.info("Week rate view has been sent", mondayInNeededWeek, person.getId());
            } catch (Exception e) {
                log.error("Failed to send week rate view to person {}, due to {} ", person.getId(), e);
            }
        }
    }
}
