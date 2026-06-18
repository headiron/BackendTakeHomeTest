package com.noom.interview.fullstack.sleep.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.noom.interview.fullstack.sleep.model.SleepLog;

public interface SleepLogRepository
        extends PagingAndSortingRepository<SleepLog, Long>{
    Page<SleepLog> findAll(Pageable pageable);

    Page<SleepLog> findByUserId(Long userId, Pageable pageable);
}
