package com.yandex.practicum.keycloack_app.exceptionhandler;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}