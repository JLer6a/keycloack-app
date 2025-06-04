package com.yandex.practicum.keycloack_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // всё, кроме API
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/login", "/register", "/error").permitAll()
                                .requestMatchers("/admin.html").hasRole("ADMIN")
                                .requestMatchers("/manager.html").hasRole("MANAGER")
                                .anyRequest().authenticated()
                                      )
                .oauth2Login(oauth2 -> oauth2
                                .defaultSuccessUrl("/authenticated.html", true)
                                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oAuth2UserService()))
                            )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        var delegate = new OidcUserService();
        return request -> {
            OidcUser user = delegate.loadUser(request);
            List<String> roles = user.getClaimAsStringList("spring_sec_roles");
            var authorities = Stream.concat(
                    user.getAuthorities().stream(),
                    roles.stream().map(SimpleGrantedAuthority::new)
                                           ).toList();

            return new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo());
        };
    }
}