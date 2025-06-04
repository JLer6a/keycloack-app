package com.yandex.practicum.keycloack_app.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String email;
    private String username;
}