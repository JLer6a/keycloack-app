package com.yandex.practicum.keycloack_app.controllers;

import com.yandex.practicum.keycloack_app.dto.JwtResponse;
import com.yandex.practicum.keycloack_app.dto.LoginRequest;
import com.yandex.practicum.keycloack_app.dto.UserRegistrationRequest;
import com.yandex.practicum.keycloack_app.exceptionhandler.EmailAlreadyUsedException;
import com.yandex.practicum.keycloack_app.exceptionhandler.InvalidCredentialsException;
import com.yandex.practicum.keycloack_app.services.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse jwt = keycloakService.login(request);
            if (jwt == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Login failed: Incorrect username or password");
            }
            return ResponseEntity.ok(jwt);
        } catch (HttpClientErrorException.Unauthorized e) {
            // Ключевой момент — логируем тело ответа
            System.err.println("401 Unauthorized: " + e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login failed: Incorrect username or password");
        } catch (HttpClientErrorException e) {
            // Показываем причину из Keycloak, если не 401
            System.err.println("Keycloak error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body("Login failed: " + e.getStatusText() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
}
