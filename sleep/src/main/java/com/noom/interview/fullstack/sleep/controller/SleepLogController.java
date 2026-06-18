package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.data.SleepLogRepository;
import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping(value="/sleep_log")
public class SleepLogController {

    @Autowired
    SleepLogRepository sleepLogRepository;

    User user = new User(1L, "headiron", "USER");

    @GetMapping
    public ResponseEntity<Page<SleepLog>> getSleepLogs(
            @PageableDefault(page=0, size=20, sort="sleep_date", direction = Sort.Direction.DESC)Pageable pageable){
        User user = this.user;
        if ( user.isAdmin())
            return ResponseEntity.ok(sleepLogRepository.findAll(pageable));
        else
            return ResponseEntity.ok(sleepLogRepository.findByUserId(user.id(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SleepLog> get(@PathVariable Long id){
        User user = this.user;
        Optional<SleepLog> sleepLogOptional = sleepLogRepository.findById(id);

        if ( sleepLogOptional.isPresent() &&
             sleepLogOptional.map(l -> user.isAdmin() || l.userId() == user.id()).orElse(false)){
            return ResponseEntity.ok(sleepLogOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SleepLog> update(@PathVariable Long id, @RequestBody SleepLog input){
        User user = this.user;
        Optional<SleepLog> sleepLogOptional = sleepLogRepository.findById(id);

        if ( sleepLogOptional.isPresent() &&
             sleepLogOptional.map(l -> user.isAdmin() || l.userId() == user.id()).orElse(false)){
            SleepLog sleepLog = sleepLogOptional.get();
            sleepLog = sleepLog.merge(input);
            return ResponseEntity.ok(sleepLogRepository.save(sleepLog));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public SleepLog post(@RequestBody SleepLog sleepLog){
        User user = this.user;

        SleepLog log = sleepLogRepository.save(sleepLog);
        return sleepLogRepository.findById(log.id()).get();
    }
}
