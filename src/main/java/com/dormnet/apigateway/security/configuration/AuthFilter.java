package com.dormnet.apigateway.security.configuration;

import com.dormnet.apigateway.security.model.User;
import com.dormnet.apigateway.security.repository.UserRepository;
import com.dormnet.apigateway.security.service.TokenService;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@NonNullApi
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (isAuthorizationHeaderInvalid(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(authHeader);
        String username = tokenService.extractUsername(token);
        User user = getUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = createAuthToken(username, user);
        setAuthenticationToken(authToken);

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorizationHeaderInvalid(@Nullable String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    private String extractToken(String authHeader) {
        return authHeader.split(" ")[1];
    }

    private User getUserByUsername(String username) {
        return Objects.requireNonNull(userRepository.findByUsername(username).orElse(null));
    }

    private UsernamePasswordAuthenticationToken createAuthToken(String username, @Nullable User user) {
        return new UsernamePasswordAuthenticationToken(username, null, user != null ? user.getAuthorities() : null);
    }

    private void setAuthenticationToken(UsernamePasswordAuthenticationToken authToken) {
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
