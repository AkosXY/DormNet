package com.dormnet.commonsecurity.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
//@Profile("test")
//public class TestSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//        return http.build();
//    }
//}
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@Profile("test")
//public class TestSecurityConfig {
//
//    private final JwtAuthenticationConverter jwtAuthenticationConverter;
//
//    public TestSecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter) {
//        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
//    }
//
//    @Bean
//    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
//        return http.build();
//    }
//}