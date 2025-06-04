?? Keycloak Integration with Spring Boot
Этот проект демонстрирует интеграцию Spring Boot с Keycloak для защиты REST API и веб-интерфейса, включая использование PKCE (Proof Key for Code Exchange). Поддерживаются как Web UI (через OAuth2 login), так и REST API (через JWT и Resource Server).

? Архитектура проекта
Проект состоит из следующих ключевых компонентов:

1. Keycloak (Auth Server)
   Используется для аутентификации и управления пользователями.

Запускается в Docker с PostgreSQL.

Импортирует realm (shop) из файла realm-export.json.

Поддерживает realm roles (ROLE_USER, ROLE_MANAGER, ROLE_ADMIN).

2. Spring Boot App
   Содержит две независимые конфигурации безопасности:

? Конфигурации безопасности
? Web UI (PKCE Login)
? Файл: WebSecurityConfig.java
? Тип: OAuth2 Login (PKCE)
?? URL-маска: /**, исключая /api/**

Особенности:

PKCE (без client secret).

Пользователи логинятся через Keycloak.

После логина попадают на /authenticated.html.

Отображаются страницы в зависимости от роли:

/admin.html — доступен ADMIN

/manager.html — доступен MANAGER

PKCE в конфигурации:

yaml
Копировать
Редактировать
client-authentication-method: none  # обязательный параметр для PKCE
authorization-grant-type: authorization_code
? REST API (JWT Bearer Token)
? Файл: ApiSecurityConfig.java
? Тип: Resource Server
?? URL-маска: /api/**

Особенности:

Аутентификация по Bearer JWT.

Проверка прав осуществляется на уровне токена.

Роли читаются из клейма spring_sec_roles.

JWT конвертер:

java
Копировать
Редактировать
converter.setJwtGrantedAuthoritiesConverter(jwt -> {
var roles = jwt.getClaimAsStringList("spring_sec_roles");
return roles.stream().map(SimpleGrantedAuthority::new).toList();
});
? AOP-аннотация @ManagerOnly
Для защиты методов API от доступа без роли MANAGER используется кастомная аннотация @ManagerOnly и аспект RoleCheckAspect.

java
Копировать
Редактировать
@Before("@annotation(com.yandex.practicum.keycloack_app.annotation.ManagerOnly)")
public void checkManagerRole(JoinPoint joinPoint) {
// выбрасывает исключение, если нет роли
}
? Регистрация пользователей
Контроллер RegistrationController позволяет зарегистрировать нового пользователя через API. Используется административный клиент Keycloak (client_credentials flow), настроенный через KeycloakAdminConfig.

Пользователю назначаются роли и отправляется email с подтверждением.

? Сервисы и обработка ошибок
Все ошибки (403, 401, 409) возвращаются в JSON-формате с читаемыми сообщениями.

Обработчики CustomAccessDeniedHandler и CustomAuthenticationEntryPoint переопределяют поведение Spring Security по умолчанию.

-------------------------------------------------------------
В ПАПКЕ ИНФОС ВСЕ КАРТИНКИ ПО СОЗДАНИЮ ЮЗЕРА ПОЧТЫ И РОЛЕЙ.
-------------------------------------------------------------

Как запустить
1. Соберите проект:
   bash
   Копировать
   Редактировать
   ./mvnw clean install
2. Запустите Keycloak и PostgreSQL:
   bash
   Копировать
   Редактировать
   docker-compose up -d
3. ЗАПУСТИТЬ KeycloackAppApplication ПРИЛОЖЕНИЕ

# Важные URL
## Адрес	Назначение
http://localhost:8081/login	OAuth2 Login через Keycloak
http://localhost:8081/api/reports	Защищённый API для MANAGER
http://localhost:8081/register	REST регистрация нового пользователя
http://localhost:8082