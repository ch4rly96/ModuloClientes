package com.example.demo.dto;

import java.util.Set;

public record AuthResponse(
        String token,
        String nombre,
        Set<String> roles
) {}