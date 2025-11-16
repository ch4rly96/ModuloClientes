package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.util.Set;

public record RegisterRequest(
        @NotBlank @Size(min = 4, max = 20) String username,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String nombre,
        @NotEmpty Set<String> roles
) {}