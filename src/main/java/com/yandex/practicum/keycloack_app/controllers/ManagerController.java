package com.yandex.practicum.keycloack_app.controllers;

import com.yandex.practicum.keycloack_app.annotation.ManagerOnly;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @ManagerOnly
    @GetMapping("/secure")
    public String secureForManagers() {
        return "Доступ только для MANAGER";
    }
}
