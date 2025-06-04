package com.yandex.practicum.keycloack_app.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}