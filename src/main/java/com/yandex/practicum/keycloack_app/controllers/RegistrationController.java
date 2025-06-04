package com.yandex.practicum.keycloack_app.controllers;

import com.yandex.practicum.keycloack_app.dto.UserRegistrationRequest;
import com.yandex.practicum.keycloack_app.exceptionhandler.EmailAlreadyUsedException;
import com.yandex.practicum.keycloack_app.services.KeycloakService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class RegistrationController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationRequest request) {
        try {
            keycloakService.registerUser(request);
            return ResponseEntity.ok("User registered successfully!");
        } catch (EmailAlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already in use: " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + e.getMessage());
        }
    }

    @GetMapping("/login")
    public void redirectToLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/keycloak");
    }
}
