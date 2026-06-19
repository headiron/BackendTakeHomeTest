package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.data.SleepLogRepository;
import com.noom.interview.fullstack.sleep.dto.SleepLogDTO;
import com.noom.interview.fullstack.sleep.dto.SleepLogStatDTO;
import com.noom.interview.fullstack.sleep.exception.NotFoundException;
import com.noom.interview.fullstack.sleep.exception.ValidationException;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.SleepLogStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SleepLogService {
    @Autowired
    SleepLogRepository sleepLogRepository;

    List<String> validFeelings = List.of("GOOD", "OK", "BAD");
    public Optional<SleepLogDTO> get(Long id) {
        return sleepLogRepository.findById(id).map(log -> convertToDTO(log));
    }

    public Page<SleepLogDTO>  getAll(Pageable pageable){
        return sleepLogRepository.findAll(pageable).map(log -> convertToDTO(log));
    }

    public Page<SleepLogDTO> getByUserId(Long userId, Pageable pageable){
        return sleepLogRepository.findByUserId(userId, pageable).map(log -> convertToDTO(log));
    }

    public SleepLogDTO insert(Long userId, SleepLogDTO sleepLogDTO) {
        validateInput(sleepLogDTO);
        SleepLog sleepLog = convertToEntity(userId, sleepLogDTO);
        Optional<SleepLog> existLog = sleepLogRepository.findByUserIdAndSleepDate(sleepLog.userId(), sleepLog.sleepDate());
        if (existLog.isPresent()) {
            throw new ValidationException("SleepLog for "+sleepLog.sleepDate()+ " already exists.(Id: "+existLog.get().id()+")");
        }
        sleepLog = sleepLogRepository.save(sleepLog);
        return convertToDTO(sleepLogRepository.findById(sleepLog.id()).get());
    }

    public SleepLogDTO update(SleepLogDTO sleepLogDTO) {
        validateInput(sleepLogDTO);
        SleepLog sleepLog = convertToEntity(sleepLogDTO.userId(), sleepLogDTO);
        Optional<SleepLog> existLog = sleepLogRepository.findByUserIdAndSleepDate(sleepLog.userId(), sleepLog.sleepDate());
        if (existLog.map(l -> l.id() != sleepLog.id()).orElse(false)) {
            throw new ValidationException("SleepLog for "+sleepLog.sleepDate()+ " already exists.(Id: "+existLog.get().id()+")");
        }
        sleepLogRepository.save(sleepLog);
        return convertToDTO(sleepLogRepository.findById(sleepLog.id()).get());
    }

    public void delete(Long id) {
        sleepLogRepository.deleteById(id);
    }

    public SleepLogStatDTO getSleepLogStat(Long userId, LocalDate startDate, LocalDate endDate) {
        SleepLogStat stat = sleepLogRepository.findAvgSleepLog(userId, startDate, endDate);
        if (stat == null) {
            throw new NotFoundException("SleepLogStat for "+startDate+" and "+endDate+" not found.");
        }
        return new SleepLogStatDTO(
                userId,
                startDate,
                endDate,
                stat.avgBedTime(),
                stat.avgWakeUpTime(),
                stat.avgSleepDuration(),
                stat.goodCount(),
                stat.okCount(),
                stat.badCount()
        );
    }

    public void validateInput(SleepLogDTO sleepLogDTO) {
        if (sleepLogDTO.sleepDate() == null) {
            throw new ValidationException("sleep_date is required");
        }
        if (sleepLogDTO.bedTime() == null) {
            throw new ValidationException("bed_time is required");
        }
        if (sleepLogDTO.wakeUpTime() == null) {
            throw new ValidationException("wake_up_time is required");
        }
        if (sleepLogDTO.morningFeeling() == null) {
            throw new ValidationException("morning_feeling is required");
        }
        if (!validFeelings.contains(sleepLogDTO.morningFeeling())){
            throw new ValidationException("morning_feeling is not valid. Only one of the following is valid:"+validFeelings);
        }

    }

    public SleepLogDTO convertToDTO(SleepLog sleepLog) {
        return new SleepLogDTO(
                sleepLog.id(),
                sleepLog.userId(),
                sleepLog.sleepDate(),
                sleepLog.bedTime(),
                sleepLog.wakeUpTime(),
                sleepLog.sleepDuration(),
                sleepLog.morningFeeling(),
                sleepLog.updatedAt()
        );
    }

    public SleepLog convertToEntity(Long userId, SleepLogDTO sleepLog) {
        return new SleepLog(
                sleepLog.id(),
                sleepLog.userId() == null ? userId : sleepLog.userId(),
                sleepLog.sleepDate(),
                sleepLog.bedTime(),
                sleepLog.wakeUpTime(),
                sleepLog.calculateSleepDuration(),
                sleepLog.morningFeeling(),
                LocalDateTime.now()
        );
    }
}
