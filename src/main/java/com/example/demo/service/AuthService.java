package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.repository.RolRepository;
import com.example.demo.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioService usuarioService,
                       RolRepository rolRepository,
                       JwtService jwtService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.jwtService = jwtService;
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

        String token = jwtService.generarToken(usuario.getUsername(), usuario.getRoles());

        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        return new AuthResponse(token, usuario.getNombre(), roles);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioService.existeUsername(request.username())) {
            throw new RuntimeException("Usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());

        Set<Rol> roles = request.roles().stream()
                .map(nombreRol -> rolRepository.findByNombre(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Rol no existe: " + nombreRol)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        usuarioService.crearUsuario(usuario);

        String token = jwtService.generarToken(usuario.getUsername(), roles);

        return new AuthResponse(token, usuario.getNombre(), request.roles());
    }
}
