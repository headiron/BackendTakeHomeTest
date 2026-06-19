package com.noom.interview.fullstack.sleep.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;

import java.time.*;
import java.time.temporal.ChronoUnit;

public record SleepLog(
        @Id Long id,
        Long userId,
        LocalDate sleepDate,
        LocalTime bedTime,
        LocalTime wakeUpTime,
        LocalTime sleepDuration,
        String morningFeeling,
        LocalDateTime updatedAt
    ) {
}
