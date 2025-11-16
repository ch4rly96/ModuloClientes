package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Usuario requerido") String username,
        @NotBlank(message = "Contraseña requerida") String password
) {}