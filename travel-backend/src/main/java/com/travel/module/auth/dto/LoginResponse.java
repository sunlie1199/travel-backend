package com.travel.module.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class LoginResponse {

    private String token;
    private Map<String, String> user;

    public static LoginResponse of(String token, String username) {
        Map<String, String> user = new HashMap<>(2);
        user.put("username", username);
        return LoginResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
