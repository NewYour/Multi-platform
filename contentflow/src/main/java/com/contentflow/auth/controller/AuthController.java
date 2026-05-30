// com.contentflow.auth.controller.AuthController.java
package com.contentflow.auth.controller;

import com.contentflow.auth.dto.LoginRequest;
import com.contentflow.auth.dto.RegisterRequest;
import com.contentflow.auth.entity.User;
import com.contentflow.auth.service.AuthService;
import com.contentflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ApiResponse.success(token);
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.register(registerRequest);
        return ApiResponse.success(user);
    }
}
