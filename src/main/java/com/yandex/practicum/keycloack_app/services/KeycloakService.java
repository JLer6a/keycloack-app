package com.yandex.practicum.keycloack_app.services;

import com.yandex.practicum.keycloack_app.dto.*;
import com.yandex.practicum.keycloack_app.exceptionhandler.EmailAlreadyUsedException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private final Keycloak keycloak;

    public void registerUser(UserRegistrationRequest request) {
        UsersResource usersResource = keycloak.realm("shop").users();

        // Проверка по email
        List<UserRepresentation> existingUsers = usersResource.search(null, null, null, request.getEmail(), 0, 1);
        if (!existingUsers.isEmpty()) {
            throw new EmailAlreadyUsedException(request.getEmail());
        }

        // Создание пользователя
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRequiredActions(List.of("VERIFY_EMAIL"));

        Response response = usersResource.create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user: " + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Установка пароля
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(request.getPassword());
        credentials.setTemporary(false);
        usersResource.get(userId).resetPassword(credentials);

        // Назначение ролей (только realm roles)
        if (request.getRoleUsers() != null && !request.getRoleUsers().isEmpty()) {
            RoleScopeResource realmRoles = usersResource.get(userId).roles().realmLevel();

            List<RoleRepresentation> roles = request.getRoleUsers().stream()
                    .map(roleName -> keycloak.realm("shop").roles().get(roleName.name()).toRepresentation())
                    .toList();

            realmRoles.add(roles);
        }

        // Отправка email с подтверждением
        usersResource.get(userId).executeActionsEmail(
                "shop-app",
                "http://localhost:8081/authenticated.html",
                List.of("VERIFY_EMAIL")
                                                     );
    }


    public JwtResponse login(LoginRequest request) {
        String tokenUrl = "http://localhost:8082/realms/shop/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "shop-app");
        params.add("client_secret", clientSecret);
        params.add("username", request.getUsername());
        params.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Login failed: " + response.getStatusCode());
        }

        Map<String, Object> tokenResponse = response.getBody();

        return new JwtResponse(
                tokenResponse.get("access_token").toString(),
                tokenResponse.get("refresh_token").toString()
        );
    }

    public UserProfileResponse getUserProfile(String userId) {
        // Логика получения профиля из Keycloak
        return null;
    }

    public void updateUserProfile(String userId, UserProfileUpdateRequest request) {
        // Логика обновления профиля в Keycloak
    }
}