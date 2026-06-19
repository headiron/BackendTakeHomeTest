package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.noom.interview.fullstack.sleep.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-unittest.properties")
@AutoConfigureMockMvc
@Transactional
public class SleepLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.mvc.format.date}")
    private String dateFormat;

    @Autowired
    private ObjectMapper objectMapper;

    User user = new User(1L, "headiron", "USER");

    @Test
    void createSleepLogValidation() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        String sleep_date = LocalDateTime.now().format(dtf);
        String bed_time = "23:00";
        String wake_up_time = "07:00";

        Map<String,String> map = new HashMap<>();
        map.put("sleep_date", sleep_date);
        map.put("bed_time", bed_time);
        map.put("wake_up_time", wake_up_time);
        map.put("morning_feeling", "GOOD");

        Map<String,String> removeFieldInfo = Map.of(
            "sleep_date", "sleep_date is required",
            "bed_time", "bed_time is required",
            "wake_up_time", "wake_up_time is required",
            "morning_feeling", "morning_feeling is required"
        );

        for(String fieldName : removeFieldInfo.keySet()) {
            String message = removeFieldInfo.get(fieldName);

            Map<String, String> withoutField = new HashMap<>(map);
            withoutField.remove(fieldName);

            String jsonWithoutField = objectMapper.writeValueAsString(withoutField);
            mockMvc.perform(post("/sleep_log")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonWithoutField))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(message));
        }

        map.put("morning_feeling", "INVALID");

        String jsonWithInvalidFeeling = objectMapper.writeValueAsString(map);
        mockMvc.perform(post("/sleep_log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithInvalidFeeling))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("morning_feeling is not valid. Only one of the following is valid:[GOOD, OK, BAD]"));

    }

    @Test
    void createSleepLog() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        String sleep_date = LocalDateTime.now().format(dtf);
        String bed_time = "23:00:00";
        String wake_up_time = "07:00:00";
        String body = """
                {
                    "sleep_date": "%s",
                    "bed_time":"%s",
                    "wake_up_time":"%s",
                    "morning_feeling":"GOOD"
                }
                """.formatted(sleep_date, bed_time, wake_up_time);
        mockMvc.perform(post("/sleep_log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sleep_date", is(sleep_date)))
                .andExpect(jsonPath("$.bed_time", is(bed_time)))
                .andExpect(jsonPath("$.wake_up_time", is(wake_up_time)))
                .andExpect(jsonPath("$.sleep_duration", is("08:00:00")))
                .andExpect(jsonPath("$.morning_feeling", is("GOOD")));
    }

    @Test
    void updateSleepLog() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        String sleepDate = LocalDateTime.now().format(dtf);
        String bedTime = "23:00:00";
        String wakeUpTime = "07:00:00";
        String body = """
                {
                    "sleep_date": "%s",
                    "bed_time":"%s",
                    "wake_up_time":"%s",
                    "morning_feeling":"GOOD"
                }
                """.formatted(sleepDate, bedTime, wakeUpTime);
        MvcResult result = mockMvc.perform(post("/sleep_log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();
        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        String updatedSleepDate = LocalDateTime.now().minusDays(1).format(dtf);
        String updatedBedTime = "01:00:00";
        String updatedWakeUpTime = "08:00:00";
        String updatedBody = """
                {
                    "sleep_date": "%s",
                    "bed_time":"%s",
                    "wake_up_time":"%s",
                    "morning_feeling":"BAD"
                }
                """.formatted(updatedSleepDate, updatedBedTime, updatedWakeUpTime);
        mockMvc.perform(put("/sleep_log/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.sleep_date", is(updatedSleepDate)))
                .andExpect(jsonPath("$.bed_time", is(updatedBedTime)))
                .andExpect(jsonPath("$.wake_up_time", is(updatedWakeUpTime)))
                .andExpect(jsonPath("$.sleep_duration", is("07:00:00")))
                .andExpect(jsonPath("$.morning_feeling", is("BAD")));
    }

    @Test
    void listSleepLog() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime sleepDate = LocalDateTime.now();
        String bed_time = "23:00";
        String wake_up_time = "07:00";

        Map<String,String> map = new HashMap<>();
        map.put("bed_time", bed_time);
        map.put("wake_up_time", wake_up_time);
        map.put("morning_feeling", "GOOD");

        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            map.put("sleep_date", sleepDate.minusDays(i).format(dtf));
            String jsonWithoutField = objectMapper.writeValueAsString(map);
            MvcResult result = mockMvc.perform(post("/sleep_log")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithoutField))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andReturn();
            Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
            ids.add(id);
        }

        mockMvc.perform(get("/sleep_log")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id", is(ids.get(0))))
                .andExpect(jsonPath("$.content.[1].id", is(ids.get(1))))
                .andExpect(jsonPath("$.content.[2].id", is(ids.get(2))));
    }
}
