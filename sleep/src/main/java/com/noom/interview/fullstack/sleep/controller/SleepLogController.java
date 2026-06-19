package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.SleepLogDTO;
import com.noom.interview.fullstack.sleep.exception.ErrorResponse;
import com.noom.interview.fullstack.sleep.exception.NotFoundException;
import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.service.SleepLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value="/sleep_log")
public class SleepLogController {

    @Autowired
    SleepLogService sleepLogService;

    User user = new User(1L, "headiron", "USER");

    @GetMapping
    public ResponseEntity<Page<SleepLogDTO>> getSleepLogs(
            @PageableDefault(page=0, size=20, sort="sleep_date", direction = Sort.Direction.DESC)Pageable pageable){
        User user = this.user;
        if ( user.isAdmin())
            return ResponseEntity.ok(sleepLogService.getAll(pageable));
        else
            return ResponseEntity.ok(sleepLogService.getByUserId(user.id(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SleepLogDTO> get(@PathVariable Long id){
        User user = this.user;
        Optional<SleepLogDTO> sleepLogOptional = sleepLogService.get(id);

        if ( sleepLogOptional.isPresent() &&
             sleepLogOptional.map(l -> user.isAdmin() || l.userId() == user.id()).orElse(false)){
            return ResponseEntity.ok(sleepLogOptional.get());
        }
        throw new NotFoundException("SleepLog "+id+" was not found.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<SleepLogDTO> update(@PathVariable Long id, @RequestBody SleepLogDTO input){
        User user = this.user;
        Optional<SleepLogDTO> sleepLogOptional = sleepLogService.get(id);

        if ( sleepLogOptional.isPresent() &&
             sleepLogOptional.map(l -> user.isAdmin() || l.userId() == user.id()).orElse(false)){
            SleepLogDTO sleepLog = sleepLogOptional.get();
            sleepLog = sleepLog.merge(input);
            return ResponseEntity.ok(sleepLogService.update(sleepLog));
        }
        throw new NotFoundException("SleepLog "+id+" was not found.");
    }

    @PostMapping
    public ResponseEntity<SleepLogDTO> post(@RequestBody SleepLogDTO sleepLogDTO){
        User user = this.user;

        SleepLogDTO result = sleepLogService.insert(user.id(), sleepLogDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        User user = this.user;
        Optional<SleepLogDTO> sleepLogOptional = sleepLogService.get(id);

        if ( sleepLogOptional.isPresent() &&
                sleepLogOptional.map(l -> user.isAdmin() || l.userId() == user.id()).orElse(false)){
            sleepLogService.delete(id);
            return ResponseEntity.noContent().build();
        }
        throw new NotFoundException("SleepLog "+id+" was not found.");
    }
}