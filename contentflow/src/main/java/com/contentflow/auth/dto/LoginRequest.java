package com.contentflow.auth.dto;
// com.contentflow.auth.dto.LoginRequest.java

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}