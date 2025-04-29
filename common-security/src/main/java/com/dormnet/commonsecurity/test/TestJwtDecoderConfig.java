package com.dormnet.commonsecurity.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Configuration
//@Profile("test")
//public class TestJwtDecoderConfig {
//
//    @Bean
//    @Primary
//    public JwtDecoder jwtDecoder() {
//        return token -> {
//            List<String> roles = new ArrayList<>();
//            if ("admin-token".equals(token)) {
//                roles.add("admin");
//            }
//            if ("user-token".equals(token)) {
//                roles.add("user");
//            }
//            return Jwt.withTokenValue(token)
//                    .header("alg", "none")
//                    .claim("email", "admin@test.com")
//                    .claim("realm_access", Map.of("roles", roles))
//                    .build();
//        };
//    }
//}