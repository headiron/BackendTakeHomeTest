package com.noom.interview.fullstack.sleep.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public record SleepLogStatDTO(
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("start_date")
        LocalDate startDate,
        @JsonProperty("end_date")
        LocalDate endDate,
        @JsonProperty("avg_bed_time")
        LocalTime avgBedTime,
        @JsonProperty("avg_wake_up_time")
        LocalTime avgWakeUpTime,
        @JsonProperty("avg_sleep_duration")
        LocalTime avgSleepDuration,
        @JsonProperty("good_count")
        Integer goodCount,
        @JsonProperty("ok_count")
        Integer okCount,
        @JsonProperty("bad_count")
        Integer badCount
    ) {
}
