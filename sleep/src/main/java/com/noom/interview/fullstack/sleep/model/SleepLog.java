package com.noom.interview.fullstack.sleep.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.*;

public record SleepLog(
        @Id Long id,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("sleep_date")
        LocalDate sleepDate,
        @JsonProperty("bed_time")
        LocalTime bedTime,
        @JsonProperty("wake_up_time")
        LocalTime wakeUpTime,
        @JsonProperty("morning_feeling")
        String morningFeeling,
        @ReadOnlyProperty
        LocalDateTime updatedAt
    ) {

        public SleepLog merge(SleepLog update) {
                return new SleepLog(
                        this.id(),
                        this.userId(),
                        update.sleepDate() != null ? update.sleepDate(): this.sleepDate(),
                        update.bedTime() != null ? update.bedTime(): this.bedTime(),
                        update.wakeUpTime() != null ? update.wakeUpTime() : this.wakeUpTime(),
                        update.morningFeeling() != null ? update.morningFeeling() : this.morningFeeling(),
                        this.updatedAt()
                );
        }
}
