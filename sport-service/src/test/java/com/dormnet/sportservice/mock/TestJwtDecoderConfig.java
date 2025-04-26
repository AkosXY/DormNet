package com.dormnet.sportservice.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TestConfiguration
public class TestJwtDecoderConfig {
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> {
            List<String> roles = new ArrayList<>();
            if (token.equals("admin-token")) roles.add("admin");
            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("email", "admin@test.com")
                    .claim("realm_access", Map.of("roles", roles))
                    .build();
        };
    }
}