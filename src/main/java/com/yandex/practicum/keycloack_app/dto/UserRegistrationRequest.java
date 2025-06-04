package com.yandex.practicum.keycloack_app.dto;

import com.yandex.practicum.keycloack_app.model.RoleUser;
import lombok.Data;

import java.util.Set;

@Data
public class UserRegistrationRequest {

    private String email;
    private String username;
    private String password;
    private Set<RoleUser> roleUsers;
}