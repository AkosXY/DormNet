package com.dormnet.apigateway.security.controller;

import com.dormnet.apigateway.security.model.AuthRequest;
import com.dormnet.apigateway.security.model.AuthResponse;
import com.dormnet.apigateway.security.model.User;
import com.dormnet.apigateway.security.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(user));
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(authRequest));
    }


}
