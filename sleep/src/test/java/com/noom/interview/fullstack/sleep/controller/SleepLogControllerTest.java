package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SleepLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    User user = new User(1L, "headiron", "USER");

    @Test
    void createSleepLog() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sleep_date = LocalDateTime.now().format(dtf);
        String bed_time = "23:00";
        String wake_up_time = "07:00";
        String body = """
                {
                    "user_id": %d,
                    "sleep_date": "%s",
                    "bed_time":"%s",
                    "wake_up_time":"%s",
                    "morning_feeling":"GOOD"
                }
                """.formatted(user.id(), sleep_date, bed_time, wake_up_time);
        mockMvc.perform(post("/sleep_log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sleep_date", is(sleep_date)))
                .andExpect(jsonPath("$.bed_time", is(bed_time+":00")))
                .andExpect(jsonPath("$.wake_up_time", is(wake_up_time+":00")))
                .andExpect(jsonPath("$.morning_feeling", is("GOOD")));
    }
}
