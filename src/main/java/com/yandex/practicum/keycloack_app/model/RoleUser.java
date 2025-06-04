package com.yandex.practicum.keycloack_app.model;

public enum RoleUser {
    ROLE_ADMIN("admin"),
    ROLE_MANAGER("manager"),
    ROLE_USER("user");

    private final String roleName;

    RoleUser(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}