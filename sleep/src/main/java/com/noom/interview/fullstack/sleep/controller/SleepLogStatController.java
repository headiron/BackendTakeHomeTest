package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.SleepLogStatDTO;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SleepLogStatController {

    @Autowired
    SleepLogService sleepLogService;

    @Value("${sleep_log.stat.interval:30}")
    int interval;

    User user = new User(1L, "headiron", "USER");

    @GetMapping(value="/sleep_log_stat")
    public ResponseEntity<SleepLogStatDTO> getSleepLogStat(
            @RequestParam(name="start_date", required = false) LocalDate startDate,
            @RequestParam(name="end_date", required = false) LocalDate endDate){
        if (endDate == null && startDate == null) {
            endDate = LocalDate.now();
            startDate = endDate.minusDays(interval);
        }
        if (startDate == null ){
            startDate = endDate.minusDays(interval);
        }
        if (endDate == null){
            endDate = startDate.plusDays(interval);
        }

        return ResponseEntity.ok(sleepLogService.getSleepLogStat(user.id(), startDate, endDate));
    }
}