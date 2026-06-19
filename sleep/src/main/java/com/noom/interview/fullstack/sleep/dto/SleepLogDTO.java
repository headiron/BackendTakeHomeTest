package com.noom.interview.fullstack.sleep.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SleepLogDTO(
        @Id Long id,
        @JsonProperty("user_id")
        Long userId,
        @JsonProperty("sleep_date")
        LocalDate sleepDate,
        @JsonProperty("bed_time")
        LocalTime bedTime,
        @JsonProperty("wake_up_time")
        LocalTime wakeUpTime,
        @ReadOnlyProperty
        @JsonProperty("sleep_duration")
        LocalTime sleepDuration,
        @JsonProperty("morning_feeling")
        String morningFeeling,

        @ReadOnlyProperty
        @JsonProperty("updated_at")
        LocalDateTime updatedAt
) {
        public LocalTime calculateSleepDuration() {
                Duration duration = null;
                if ( bedTime.isAfter(wakeUpTime)) {
                        LocalDateTime start = LocalDateTime.of(sleepDate, bedTime);
                        LocalDateTime end = LocalDateTime.of(sleepDate.plusDays(1), wakeUpTime);
                        duration = Duration.between(start, end);
                }
                else{
                        duration = Duration.between(bedTime, wakeUpTime);
                }
                return LocalTime.MIDNIGHT.plus(duration);
        }

        public SleepLogDTO merge(SleepLogDTO update) {
                return new SleepLogDTO(
                        this.id(),
                        this.userId(),
                        update.sleepDate() != null ? update.sleepDate(): this.sleepDate(),
                        update.bedTime() != null ? update.bedTime(): this.bedTime(),
                        update.wakeUpTime() != null ? update.wakeUpTime() : this.wakeUpTime(),
                        null,
                        update.morningFeeling() != null ? update.morningFeeling() : this.morningFeeling(),
                        this.updatedAt()
                );
        }
}
