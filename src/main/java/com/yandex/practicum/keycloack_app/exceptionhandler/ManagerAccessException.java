package com.yandex.practicum.keycloack_app.exceptionhandler;

public class ManagerAccessException extends RuntimeException {

    public ManagerAccessException(String message) {
        super(message);
    }
}