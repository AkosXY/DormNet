package com.dormnet.apigateway.security.service;

import com.dormnet.apigateway.security.model.AuthRequest;
import com.dormnet.apigateway.security.model.AuthResponse;
import com.dormnet.apigateway.security.model.User;
import com.dormnet.apigateway.security.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private final PasswordEncoder passwordEncoder;


    public AuthResponse login(AuthRequest authRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword()
        );
        authenticationManager.authenticate(authToken);
        Optional<User> optionalUser = userRepository.findByUsername(authRequest.getUsername());
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        String jwt = tokenService.generateToken(user, generateExtraClaims(user));
        return new AuthResponse(jwt);
    }

    public AuthResponse signup(User user) {
        user.setName(user.getName());
        user.setUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        userRepository.save(user);
        String token = tokenService.generateToken(user, generateExtraClaims(user));
        return  new AuthResponse(token);
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().name());
        return extraClaims;
    }
}
