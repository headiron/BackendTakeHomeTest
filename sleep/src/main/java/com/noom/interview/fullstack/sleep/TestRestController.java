package com.noom.interview.fullstack.sleep;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

    @GetMapping("/test")
    String test() {
        return  "Hello world!";
    }
}
