package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioService usuarioService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::name)
                .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(usuario.getUsername(), roles);

        return new AuthResponse(token, usuario.getNombre(), roles);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioService.existeUsername(request.username())) {
            throw new RuntimeException("Usuario ya existe");
        }

        Set<Rol> roles = request.roles().stream()
                .map(Rol::valueOf)
                .collect(Collectors.toSet());

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setRoles(roles);

        usuarioService.crearUsuario(usuario);

        String token = jwtUtil.generateToken(usuario.getUsername(), request.roles());
        return new AuthResponse(token, usuario.getNombre(), request.roles());
    }
}
