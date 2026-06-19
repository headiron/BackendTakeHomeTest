package com.noom.interview.fullstack.sleep.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record SleepLogStat(
        LocalTime avgBedTime,
        LocalTime avgWakeUpTime,
        LocalTime avgSleepDuration,
        Integer goodCount,
        Integer okCount,
        Integer badCount
    ) {
}
