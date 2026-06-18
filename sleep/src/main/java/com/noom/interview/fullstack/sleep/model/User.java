package com.noom.interview.fullstack.sleep.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

public record User(
        @Id Long id,
        @JsonProperty("username")
        String username,
        @JsonProperty("usertype")
        String usertype) {
    public boolean isAdmin() {
        return usertype.equals("ADMIN");
    }
}
