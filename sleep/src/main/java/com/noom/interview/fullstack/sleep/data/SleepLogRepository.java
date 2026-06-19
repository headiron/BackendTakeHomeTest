package com.noom.interview.fullstack.sleep.data;

import com.noom.interview.fullstack.sleep.model.SleepLogStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SleepLogRepository
        extends PagingAndSortingRepository<SleepLog, Long>{

    Optional<SleepLog> findByUserIdAndSleepDate(Long userId, LocalDate sleepDate);

    Page<SleepLog> findAll(Pageable pageable);

    Page<SleepLog> findByUserId(Long userId, Pageable pageable);

    @Query("""
              SELECT AVG(bed_time)::time AS avg_bed_time, AVG(wake_up_time)::time AS avg_wake_up_time
                   , AVG(sleep_duration)::time AS avg_sleep_duration
                   , SUM( CASE WHEN morning_feeling = 'GOOD' THEN 1 ELSE 0 END ) AS good_count
                   , SUM( CASE WHEN morning_feeling = 'OK' THEN 1 ELSE 0 END ) AS ok_count
                   , SUM( CASE WHEN morning_feeling = 'BAD' THEN 1 ELSE 0 END ) AS bad_count
              FROM sleep_log
              WHERE user_id = :user_id AND sleep_date >= :start_date
                                       AND sleep_date <= :end_date
              GROUP BY user_id
    """)
    SleepLogStat findAvgSleepLog(@Param("user_id") Long userId, @Param("start_date") LocalDate startDate, @Param("end_date") LocalDate endDate);

}
